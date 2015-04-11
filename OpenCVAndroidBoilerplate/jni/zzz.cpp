#include <com_example_opencvandroidboilerplate_MainActivity.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <stdio.h>
#include <sys/time.h>


#include <sys/sysinfo.h>
#include <sys/resource.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "keymatch", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "ProjectName", __VA_ARGS__)
#define LAST_ON_SIZE (50)

// ring image buffer
// various tracking parameters (in seconds)
const double MHI_DURATION = 0.5;
const double MAX_TIME_DELTA = 0.5;
const double MIN_TIME_DELTA = 0.05;
const int N = 3;
double starttimestamp = 0.0;
//
const int CONTOUR_MAX_AERA = 300;

IplImage **buf = 0;
int last = 0;
IplImage *mhi = 0; // MHI: motion history image
IplImage *inimg = 0;
IplImage *oimg = 0;
IplImage *motion = 0;
int nFrmNum = 0;
//double lasttimestamp = 0.00001;
struct AvTrackBlock{
		int Direction;
		int FramesTracked;
		int avgX;
		int avgY;
		std::vector<std::pair<int, int> > track_points;
		int status;

}TrackBlock[30];
struct DV
{
	int d0;
	int d1;
	int d2;
	int v1;
	int v2;
};


std::vector<DV> v_last_on;

int No = 0;
CvFont font1;//初始化字体格式
CvFont font2;
double getAngle(std::pair<int, int> v1, std::pair<int, int> v2);
bool isCollinear(std::vector<std::pair<int, int> > v, int area);
int update_mhi( IplImage* img, IplImage* dst, int diff_threshold , int nFrmNum, DV& dv);
double getCurrentTime();
bool get_info(std::string& addr);
bool get_info2(std::string& addr);
std::string&   replace_all(std::string&   str,const   std::string&   old_value,const   std::string&   new_value);
int  displayResult( IplImage* img, IplImage* dst, DV dv);

std::string ma2 = "5";
std::string mb2 = "03";
std::string mc2 = "cc";
std::string md2 = "44";
std::string me2 = "dc";
std::string mf2 = "02";
std::string mg2 = "0";

DV getLightSwitch(DV dv_in)
{
	DV dv_out;
	dv_out.d0 = 0;
	dv_out.d1 = 0;
	dv_out.d2 = 0;
	dv_out.v1 = 0;
	dv_out.v2 = 0;
	v_last_on.erase(v_last_on.begin());
	v_last_on.push_back(dv_in);
	for(int i = 0; i < LAST_ON_SIZE; i++)
	{
		if(v_last_on[i].d1 > 0)
		{
			dv_out.d1 = 1;
			dv_out.v1 = dv_out.v1>v_last_on[i].v1?dv_out.v1:v_last_on[i].v1;
		}
		if(v_last_on[i].d2 > 0)
		{
			dv_out.d2 = 1;
			dv_out.v2 = dv_out.v2>v_last_on[i].v2?dv_out.v2:v_last_on[i].v2;
		}
		dv_out.d0 = dv_out.d0>v_last_on[i].d0?dv_out.d0:v_last_on[i].d0;
	}
	if(dv_out.d1 == 1 || dv_out.d2 == 1)
		dv_out.d0 = 0;

	return dv_out;
}

JNIEXPORT jobject JNICALL Java_com_example_opencvandroidboilerplate_MainActivity_zzz
   ( JNIEnv * env, jobject obj, jlong addrInRGBA , jlong addrOut, jobject m_obj)
{
	std::string xx = "";
	if(nFrmNum == 0)
	{
		std::string res = ma2+mb2+mc2+md2+me2+mf2+mg2+"\n";
//		if(1)//get_info(xx) && replace_all(xx, ":","").compare(res) == 0)
		if(get_info2(xx) && xx.compare("0702870915871") == 0)
			LOGI( "Goose 1");
		else
		{
			LOGI( "Goose 0");//, xx.c_str(), res.c_str());
			return m_obj;
		}
	}
	int tagOut = 0;
	int x = 1;
//	LOGI( "Goose in processFrame 1:%d", nFrmNum);
     cv::Mat * pMatInRGBA = ( cv::Mat * ) addrInRGBA ;
     cv::Mat * pMatOut = ( cv::Mat * ) addrOut ;

     IplImage timg = *pMatInRGBA;
     inimg = &timg;
//     LOGI( "Goose in processFrame 2:(%d,%d)", inimg->width, inimg->height);

     if( inimg )
     {
         if( !motion )
         {
        	 motion = cvCreateImage( cvSize(inimg->width,inimg->height), 8, 1 );
        	 cvZero( motion );
             motion->origin = inimg->origin;
         }
     }
     if(nFrmNum == 0)
     {
    	 starttimestamp = getCurrentTime()-1;
    	 nFrmNum = 0;
    	 cvInitFont(&font1, CV_FONT_HERSHEY_SIMPLEX, 0.5, 0.5, 0, 1, 8);
    	 cvInitFont(&font2, CV_FONT_HERSHEY_SIMPLEX, 1, 1, 0, 2, 8);
    	 for(int j=0;j<30;j++)
    	 {
    	 	TrackBlock[j].Direction=0;
    	 	TrackBlock[j].FramesTracked=0;
    	 	TrackBlock[j].avgX=0;
    	 	TrackBlock[j].avgY=0;
    	 	TrackBlock[j].status=0;
    	 }
    	 v_last_on.clear();
    	 for(int i = 0; i < LAST_ON_SIZE; i++)
    	 {
    		 DV tmp_dv;
    		 tmp_dv.d0 = 0;
    		 tmp_dv.d1 = 0;
    		 tmp_dv.d2 = 0;
    		 tmp_dv.v1 = 0;
    		 tmp_dv.v2 = 0;
    		 v_last_on.push_back(tmp_dv);
    	 }
     }
     nFrmNum++;
     DV dv;
     tagOut = update_mhi( inimg, motion, 60 , nFrmNum, dv);
//     LOGI( "Goose in processFrame 5");
     dv = getLightSwitch(dv);
//     LOGI("Goose jni : (%d,%d,%d,%d,%d)", dv.d0, dv.d1,dv.d2, dv.v1, dv.v2);
     displayResult( inimg, motion, dv);

     jclass    m_cls   = env->FindClass("com/example/opencvandroidboilerplate/DV");
     jfieldID  m_d0 = env->GetFieldID(m_cls,"d0","I");
     jfieldID  m_d1 = env->GetFieldID(m_cls,"d1","I");
     jfieldID  m_d2 = env->GetFieldID(m_cls,"d2","I");
     jfieldID  m_v1 = env->GetFieldID(m_cls,"v1","I");
     jfieldID  m_v2 = env->GetFieldID(m_cls,"v2","I");

     env->SetIntField(m_obj,m_d0, dv.d0);
     env->SetIntField(m_obj,m_d1, dv.d1);
     env->SetIntField(m_obj,m_d2, dv.d2);
	 env->SetIntField(m_obj,m_v1, dv.v1);
     env->SetIntField(m_obj,m_v2, dv.v2);

     if(tagOut > 0)
     {
    	 *pMatOut = motion;//
     }
     else
     {

    	 *pMatOut = inimg;
     }
     return m_obj;
}

int  displayResult( IplImage* img, IplImage* dst, DV dv)
{
	std::stringstream ss;
	std::string str1;
	ss << dv.v1;
	ss >> str1;
	std::stringstream ss2;
	std::string str2;
	ss2 << dv.v2;
	ss2 >> str2;

	cvPutText(img,str1.c_str(), cvPoint(150, 200), &font2, cvScalar(255,0,0));
	cvPutText(dst,str1.c_str(), cvPoint(150, 200), &font2, cvScalar(255,0,0));
	cvPutText(img,str2.c_str(), cvPoint(250, 200), &font2, cvScalar(255,0,0));
	cvPutText(dst,str2.c_str(), cvPoint(250, 200), &font2, cvScalar(255,0,0));
	return 0;
}

double getAngle(std::pair<int, int> v1, std::pair<int, int> v2)
{
	double selfmulti = (v1.first * v2.first + v1.second * v2.second);
	double dist = (std::sqrt(v1.first * v1.first * 1.0 + v1.second * v1.second * 1.0) * std::sqrt(v2.first * v2.first * 1.0 + v2.second * v2.second * 1.0));
	double cos = selfmulti / dist;
//	fprintf(stderr, "getAngle:%lf %lf %lf %lf\n", selfmulti, dist, cos, std::acos(cos) /3.14*180);
	//LOGI("Goose getAngle dist:%lf selfmulti:%lf cos:%lf\n", dist, selfmulti, cos);
	if(cos < -1 && cos > -2)
		cos = -1;
	else if(cos > 1 && cos < 2)
		cos = 1;
	return std::acos(cos) /3.14*180;
}

int getDirection(std::vector<std::pair<int, int> > v)
{
	float k1 = 0.0;
	std::pair<int, int> vect1 = std::make_pair<int, int>(v[v.size()-1].first - v[0].first, v[v.size()-1].second - v[0].second);
	std::pair<int, int> vect2 = std::make_pair<int, int>(1, -1);
	double angle = getAngle(vect1, vect2);

					//fprintf(stderr, "result2:(%d,%d)(%d,%d)(%d,%d) angle:%lf\n", \
						v[0].first, v[0].second, v[i].first, v[i].second, v[v.size()-1].first, v[v.size()-1].second, angle);
//	LOGI("Goose getDirection (%d,%d)(%d,%d)", v[0].first, v[0].second, v[v.size()-1].first, v[v.size()-1].second);
//	LOGI("Goose getDirection angle:%lf\n", angle);
	if(angle > 120)
	{
		return -1;
	}
	else if(angle < 60)
	{
		return 1;
	}
	else
		return 0;
}

int getVelocity(std::vector<std::pair<int, int> > v)
{
	float square = (v[v.size()-1].first - v[0].first)*(v[v.size()-1].first - v[0].first)+(v[v.size()-1].second - v[0].second)*(v[v.size()-1].second - v[0].second);
	float velocity = std::sqrt(square) / v.size();
	//fprintf(stderr, "getVelocity v:%f\n", velocity);
	return (int)velocity;
}

bool isCollinear(std::vector<std::pair<int, int> > v, int area)
{
	int MOVE_MIN_DIST = 500;
	float k1 = 0.0;
	bool dist = false, result = true;
	int r1 = (v[v.size() - 1].first - v[0].first) * (v[v.size() - 1].first - v[0].first);
	int r2 = (v[v.size() - 1].second - v[0].second) * (v[v.size() - 1].second - v[0].second);
//	fprintf(stderr, "1:%d 2:%d 3:%d 4:%d r1:%d r2:%d\n", \
		v[v.size() - 1].first, v[0].first, v[v.size() - 1].second, v[0].second, r1, r2);

	if(area > 5000)
		MOVE_MIN_DIST = 10;
	else
		MOVE_MIN_DIST = 10;
	if(r1 + r2 > MOVE_MIN_DIST)
		dist = true;
	if(dist)
	{
		if(v[v.size() - 1].first - v[0].first != 0)
			k1 = (v[v.size() - 1].second - v[0].second) / (v[v.size() - 1].first - v[0].first+0.01);
		for(int i = 1; i< v.size()-1; i++)
		{
			float k2 = 0.0;
			if(v[i].first - v[0].first != 0)
				k2 = (v[i].second - v[0].second) / (v[i].first - v[0].first+0.01);
			float tan = (k2-k1)/(1+k1*k2);
			float angle = std::atan(tan)/3.14*180;
			//fprintf(stderr, "result1:(%d,%d)(%d,%d) k1:%lf k2:%lf tan:%lf angle:%lf\n", \
				v[0].first, v[0].second, v[i].first, v[i].second, k1, k2,  tan, angle);
			if(angle > 90)
			{
				result = false;
				break;
			}
		}
		if(result)
			for(int i = 1; i< v.size()-1; i++)
			{
				std::pair<int, int> vect1 = std::make_pair<int, int>(v[i].first - v[0].first, v[i].second - v[0].second);
				std::pair<int, int> vect2 = std::make_pair<int, int>(v[v.size()-1].first - v[i].first, v[v.size()-1].second - v[i].second);
				double angle = getAngle(vect1, vect2);

				//fprintf(stderr, "result2:(%d,%d)(%d,%d)(%d,%d) angle:%lf\n", \
					v[0].first, v[0].second, v[i].first, v[i].second, v[v.size()-1].first, v[v.size()-1].second, angle);
				if(angle > 90 )
				{
					result = false;
					break;
				}
			}
	}
	else
		result = false;
//	if(result)
//		fprintf(stderr, "\nresult:true");
//	if(1)//!result)
//	{
//		fprintf(stderr, "\nisCollinear:");
////		LOGI("Goose isCollinear");
//		for(int i = 0; i< v.size(); i++)
//		{
//			fprintf(stderr, "(%d,%d)", v[i].first, v[i].second);
//		}
//		fprintf(stderr, "result:(%d,%d)\n", dist, result);
////		LOGI("Goose isCollinear result:(%d,%d)\n", dist, result);
//	}
	return result;
}



std::string f1 = "ress";
std::string e1 = "an0/add";
std::string d1 = "et/wl";
std::string c1 = "ass/n";
std::string b1 = "ys/cl";
std::string a1 = "/s";
int  update_mhi( IplImage* img, IplImage* dst, int diff_threshold , int nFrmNum, DV& dv)
{
	int foundMoving = 0;
	//DV dv;
	dv.d0 = 0;
	dv.d1 = 0;
	dv.d2 = 0;
	dv.v1 = 0;
	dv.v2 = 0;
	clock_t xx = clock();
    double timestamp = getCurrentTime()-starttimestamp;//xx/100.;//
//    LOGI("Goose update_mhi clock:%lf timestamp:%lf ps:%d\n", xx/100., timestamp, CLOCKS_PER_SEC);

    CvSize size = cvSize(img->width,img->height);
    int i, j, idx1, idx2;
    IplImage* silh;
    uchar val;
    float temp;
    IplImage tmpresult;
    IplImage* pyr = cvCreateImage( cvSize((size.width & -2)/2, (size.height & -2)/2), 8, 1 );
    CvMemStorage *stor;
    CvSeq *cont, *result, *squares;
    CvSeqReader reader;
    if( !mhi || mhi->width != size.width || mhi->height != size.height )
    {
        if( buf == 0 )
        {
            buf = (IplImage**)malloc(N*sizeof(buf[0]));
            memset( buf, 0, N*sizeof(buf[0]));
        }

        for( i = 0; i < N; i++ )
        {
            cvReleaseImage( &buf[i] );
            buf[i] = cvCreateImage( size, IPL_DEPTH_8U, 1 );
            cvZero( buf[i] );
        }
        cvReleaseImage( &mhi );
        mhi = cvCreateImage( size, IPL_DEPTH_32F, 1 );
        cvZero( mhi );
    }
    //add to handle the clock() optimaize out issue

    cvCvtColor( img, buf[last], CV_BGR2GRAY );

    idx1 = last;
    idx2 = (last + 1) % N;
    last = idx2;

    silh = buf[idx2];
    cvAbsDiff( buf[idx1], buf[idx2], silh );
    cvThreshold( silh, silh, 20, 255, CV_THRESH_BINARY );
    cvUpdateMotionHistory( silh, mhi, timestamp, MHI_DURATION );

//    cvCvtScale( mhi, dst, 255./MHI_DURATION,
//      (MHI_DURATION - timestamp)*255./MHI_DURATION );
    cvCvtScale( mhi, dst, 255./MHI_DURATION, 0 );
    cvSmooth( dst, dst, CV_MEDIAN, 3, 0, 0, 0 );
    cvPyrDown( dst, pyr, 7 );
    cvDilate( pyr, pyr, 0, 1 );
    cvPyrUp( pyr, dst, 7 );
    stor = cvCreateMemStorage(0);
    cont = cvCreateSeq(CV_SEQ_ELTYPE_POINT, sizeof(CvSeq), sizeof(CvPoint) , stor);
    cvFindContours( dst, stor, &cont, sizeof(CvContour),
                    CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
    for(;cont;cont = cont->h_next)
    {
		CvRect bndRect=cvRect(0,0,0,0);
		int avgX = 0;
		int avgY = 0;
		bool FindCar=false;
		bndRect = cvBoundingRect(cont, 0);
		avgX = (bndRect.x + bndRect.x + bndRect.width) / 2;
		avgY = (bndRect.y + bndRect.y + bndRect.height) / 2;
        CvRect r = ((CvContour*)cont)->rect;
		int area = r.height * r.width;
//		LOGI("Goose try CvContour:(%d,%d)%d\n", r.x, r.y, area);
        if(area > CONTOUR_MAX_AERA && r.height > 10 && r.width > 10)
			//&& avgX > 70 && avgX < 350 && avgY < 170 && avgY > 10)
        {
//        	LOGI("Goose found CvContour:(%d,%d)\n", r.x, r.y);
//			cvRectangle( img, cvPoint(r.x,r.y),cvPoint(r.x + r.width, r.y + r.height),CV_RGB(255,0,0), 1, 8,0);//CV_AA
			for(int i=0;i<30;i++)
			{
				if(TrackBlock[i].avgX !=0 && abs(avgX-TrackBlock[i].avgX)<30 && abs(avgY-TrackBlock[i].avgY)<50)
				{
					TrackBlock[i].FramesTracked=nFrmNum;
					TrackBlock[i].avgX=avgX;
					TrackBlock[i].avgY=avgY;
					(TrackBlock[i].track_points).push_back(std::pair<int, int>(avgX, avgY));
					TrackBlock[i].status=0;

					if((TrackBlock[i].track_points).size() == 17 && isCollinear(TrackBlock[i].track_points, area))
					{
//						LOGI("Goose found inline:fm(%d)(%d,%d)\n", nFrmNum, r.x, r.y);
						cvPutText(img,"inline", cvPoint(r.x, r.y), &font1, cvScalar(255,0,0));
						TrackBlock[i].status=1;
					}
					else if((TrackBlock[i].track_points).size() > 17)
					{
						std::vector<std::pair<int, int> >::iterator itr = (TrackBlock[i].track_points).begin();
						(TrackBlock[i].track_points).erase(itr);
						if(isCollinear(TrackBlock[i].track_points, area))
						{
//							LOGI("Goose found minline:fm(%d)(%d,%d)\n", nFrmNum, r.x, r.y);
							cvPutText(img,"minline", cvPoint(r.x, r.y), &font1, cvScalar(255,0,0));
							TrackBlock[i].status=1;
						}
						else
						{
//							LOGI("Goose found retrack:fm(%d)(%d,%d)\n", nFrmNum, r.x, r.y);
							cvPutText(img,"notinline", cvPoint(r.x, r.y), &font1, cvScalar(0,0,255));
							TrackBlock[i].status=0;
						}
					}
					else
					{
//						LOGI("Goose found tracking:fm(%d)(%d,%d)\n", nFrmNum, r.x, r.y);
						cvPutText(img,"tracking", cvPoint(r.x + r.width/2, r.y + r.height/2), &font1, cvScalar(0,255,0));
						TrackBlock[i].status=0;
					}
					i=30;
					FindCar=true;
				}
			}
			if(FindCar!=true)
			{
				TrackBlock[No].Direction=1;
				TrackBlock[No].FramesTracked=nFrmNum;
				TrackBlock[No].avgX=avgX;
				TrackBlock[No].avgY=avgY;
				TrackBlock[No].status=0;
				if(No==29){
					No=0;
				}
				else
					No++;
			}
			FindCar=false;
		}
    }
	for(int j=0;j<30;j++)
	{
		if(TrackBlock[j].FramesTracked >= nFrmNum-2
			&& (TrackBlock[j].track_points).size() > 2
			&& TrackBlock[j].status==1)
		{
			int d = getDirection(TrackBlock[j].track_points);
			int v = getVelocity(TrackBlock[j].track_points);
			if(d == 0)
				dv.d0 = 1;
			else if(d == 1)
			{
				dv.d1 = 1;
				if(v > dv.v1)
					dv.v1 = v;
			}
			else if(d == -1)
			{
				dv.d2 = 1;
				if(v > dv.v2)
					dv.v2 = v;
			}
//			LOGI( "Goose getDirection: nFrmNum:%d d:%d r:%d", nFrmNum, d, getLightSwitch(dv));
			foundMoving = 1;
		}
		if(TrackBlock[j].FramesTracked < nFrmNum-2)
		{
			TrackBlock[j].Direction=0;
			TrackBlock[j].FramesTracked=0;
			TrackBlock[j].avgX=0;
			TrackBlock[j].avgY=0;
			(TrackBlock[j].track_points).clear();
			TrackBlock[j].status = 0;
		}
	}
//	fprintf(stderr, "nFrmNum:%d foundMoving:%d\n", nFrmNum, foundMoving);
	cvPutText(img,"xxxxxxx", cvPoint(10, 10), &font1, cvScalar(255,0,0));
		cvPutText(img,"yyyyyyy", cvPoint(100, 100), &font1, cvScalar(255,255,0));
    cvReleaseMemStorage(&stor);
    cvReleaseImage( &pyr );

    //foundMoving  = getLightSwitch(dv);
//    LOGI( "Goose result: nFrmNum:%d foundMoving:%d (%d,%d,%d,%d,%d)\n", nFrmNum, foundMoving, dv.d0, dv.d1, dv.d2, dv.v1, dv.v2);
	return foundMoving;
}


double getCurrentTime()
{
   struct timeval tv;
   gettimeofday(&tv,NULL);
   return (tv.tv_sec * 10. + tv.tv_usec / 100000.)-14000000000;
}


bool get_info(std::string& addr)
{
//"/sys/class/net/wlan0/address"
	std::string x = a1+b1+c1+d1+e1+f1;
    FILE* fp = fopen(x.c_str(), "r");
    if (0 == fp) return false;

    char line[100];

    // 跳过头两行
    while (fgets(line, sizeof(line)-1, fp))
    {
    	addr = line;

        return true;
    }
    fclose(fp);
    return false;
}
bool get_info2(std::string& addr)
{
//"/sys/class/net/wlan0/address"
	std::string x = "/system/info";
    FILE* fp = fopen(x.c_str(), "r");
    if (0 == fp) return false;

    char line[100];

    // 跳过头两行
    while (fgets(line, sizeof(line)-1, fp))
    {
    	addr = line;

        return true;
    }
    fclose(fp);
    return false;
}
std::string&   replace_all(std::string&   str,const   std::string&   old_value,const   std::string&   new_value)
{
    while(true)   {
        std::string::size_type   pos(0);
        if(   (pos=str.find(old_value))!=std::string::npos   )
            str.replace(pos,old_value.length(),new_value);
        else   break;
    }
    return   str;
}

