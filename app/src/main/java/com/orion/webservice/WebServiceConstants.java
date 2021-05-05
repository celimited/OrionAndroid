package com.orion.webservice;

public class WebServiceConstants {

    public static final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    public static final String Method_SaveOrderMasterInfo = "SaveOrderMasterInfo";
    //	public static final  String Method_SaveOrderDetailsInfo = "SaveOrderDetailsInfo";
    public static final String Method_SaveOrderDetailsInfo = "SaveOrder";
    public static final String Method_SaveNewOutletDetailsInfo = "SaveNewOutlet";
    public static final String Method_GetBrands = "GetBrand";
    public static final String Method_GetChannels = "GetChannel";
    public static final String Method_GetHHTUsers = "GetHHTUsers";
    public static final String Method_GetDSRBasicInfos = "GetDSRBasicInfos";
    public static final String Method_GetSKUs = "GetSKUs";
    public static final String Method_GetSection = "GetSection";
    public static final String Method_GetNoOrderReasonInfo = "GetNoOrderReasonInfo";
    public static final String Method_GetMarketReturnReason = "GetMarketReturns";
    public static final String Method_GetOutletInfos = "GetOutletInfos";
    public static final String Method_GetTPR = "GetSalesPromotions";
    public static final String Method_GetTPRSKUChnl = "GetSPSKUChannel";
    public static final String Method_GetTPRSlab = "GetSPSlabs";
    public static final String Method_GetSlabItem = "GetSPBonuses";
    public static final String Method_GetDSRDailyTargetInfos = "GetDSRDailyTargetInfos";

    public static final String PARAMETER_NAME = "Object";

    //public static final String SOAP_ADDRESS = "http://185.19.217.10/OnlineSalesSrvc/OnlineSales.asmx";
    //public static final String SOAP_ADDRESS = "http://10.0.2.2:26006/DMS_Web/WebService.asmx";
    //public static final String SOAP_ADDRESS = "http://185.19.217.10/AKCGWebService/WebService.asmx";
    //public static final String SOAP_ADDRESS = "http://orionbd.com/orionsales/OrionSales.asmx";
    public static final String SOAP_ADDRESS = "http://37.61.216.162/Orion_WebService/OrionSales.asmx";  //Test

    //public static final String SOAP_ADDRESS = "http://182.160.122.60/Orion_WebService/OrionSales.asmx"; //Client
    //public static final String SOAP_ADDRESS = "http://localhost:12833/OrionSales.asmx"; //Localhost

    //public static final String SOAP_ADDRESS = "http://10.0.2.2:7489/OnlineSales.asmx" ;
    //public static final String SOAP_ADDRESS = "http://Web.abulkhairgroup.com/dms/webservice.asmx";
    //public static final String SOAP_ADDRESS = "http://10.10.50.133/dms/WebService.asmx";
//	static
//	{
//		String webAddress = new MainActivity().readAddress();
//		SOAP_ADDRESS = webAddress;
//	}

    //
}
