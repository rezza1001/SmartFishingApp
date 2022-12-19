package com.vma.smartfishingapp.libs;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DistanceUnit {

    private double mDistance = 0;
    private String unit = "Nm";

    public DistanceUnit (double distanceKm){
        mDistance = distanceKm;
    }

    public double getDistance(){
        if (unit.equalsIgnoreCase("Nm")){
            mDistance = mDistance / 1.852;
        }
        else if (unit.equalsIgnoreCase("Mi")){
            mDistance = mDistance * 1.151;
        }
        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.UP);
        mDistance =  Double.parseDouble(df.format(mDistance).replaceAll(",","."));
        return mDistance;
    }

    public String getDisplay(){
        return getDistance()+" "+unit;
    }

}
