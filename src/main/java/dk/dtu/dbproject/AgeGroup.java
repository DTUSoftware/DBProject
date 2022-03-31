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
}
