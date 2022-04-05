package dk.dtu.dbproject;

public class AgeGroup {
    private int lowerAge;
    private int upperAge;

    public AgeGroup(int lowerAge, int upperAge) {
        this.lowerAge = lowerAge;
        this.upperAge = upperAge;
    }

    public int getLowerAge() {
        return lowerAge;
    }

    public int getUpperAge() {
        return upperAge;
    }

    public String toString() {
        return "AgeGroup{" +
                "lowerAge=" + lowerAge +
                ", upperAge=" + upperAge +
                '}';
    }
}
