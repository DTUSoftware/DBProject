package dk.dtu.dbproject;

public class AgeGroup {
    private int ageGroupID;
    private int lowerAge;
    private int upperAge;

    public AgeGroup(int ageGroupID, int lowerAge, int upperAge) {
        this.ageGroupID = ageGroupID;
        this.lowerAge = lowerAge;
        this.upperAge = upperAge;
    }


    public int getAgeGroupID() {
        return ageGroupID;
    }

    public int getLowerAge() {
        return lowerAge;
    }

    public int getUpperAge() {
        return upperAge;
    }

    public String toString() {
        return "AgeGroup{" +
                "ageGroupID=" + ageGroupID +
                ", lowerAge=" + lowerAge +
                ", upperAge=" + upperAge +
                '}';
    }
}
