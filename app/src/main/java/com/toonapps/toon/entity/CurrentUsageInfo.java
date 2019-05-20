package com.toonapps.toon.entity;

public class CurrentUsageInfo {

    private String result;
    private PowerProduction powerProduction;
    private GasUsage gasUsage;
    private PowerUsage powerUsage;



    public boolean getResult() {
        return result.equals("ok");
    }

    public PowerUsage getPowerUsage(){
        return powerUsage;
    }

    public PowerProduction getPowerProduction() {
        return powerProduction;
    }

    public GasUsage getGasUsage() {
        return gasUsage;
    }

    public class PowerUsage {
        private float value;
        private float avgValue;

        public float getValue() {
            return value;
        }

        public float getAvgValue() {
            return avgValue;
        }
    }

    public class PowerProduction {
        private float value;
        private float avgValue;

        public float getValue() {
            return value;
        }

        public float getAvgValue() {
            return avgValue;
        }
    }

    public class GasUsage {
        private float value;
        private float avgValue;

        public float getValue() {
            return value;
        }

        public float getAvgValue() {
            return avgValue;
        }
    }
}
