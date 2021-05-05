package com.orion.entities;

import java.io.Serializable;

/**
 * Created by shahriar on 8/14/16.
 */
public class Image implements Serializable {
    public static final int IMAGE_TYPE_OUTLET_FRONT = 0;
    public static final int IMAGE_TYPE_PRODUCT_SHELF = 1;
    public static final int IMAGE_TYPE_PROMO_PROGRAM = 2;

    public int imageId;
    public String outletId;
    public int imageType;
    public String imageUrl;
    public long imageCaptureTime;
    public String outletlatitude = "0.0";
    public String outletlongitude = "0.0";

}
