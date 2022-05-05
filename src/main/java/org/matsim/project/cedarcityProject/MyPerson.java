package org.matsim.project.cedarcityProject;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.utils.geometry.CoordinateTransformation;

import java.util.Random;

public class MyPerson {
    private static final Logger log = Logger.getLogger(MyPerson.class);

    String gender;
    Integer age;
    Boolean worker;
    Boolean student;
    String dap;
    Coord homeLocation;
    Id<Person> id;

    Scenario sc;
    PopulationFactory pf;
    CoordinateTransformation ct;

    public MyPerson(String gender, Integer age){
        this.gender = gender;
        this.age = age;
    }

    public MyPerson(Integer id, Random r, Scenario sc, PopulationFactory pf, CoordinateTransformation ct){
        this.id = Id.createPersonId(id);
        this.sc = sc;
        this.pf = pf;
        this.ct = ct;

        Boolean gendercoin = r.nextBoolean();
        if(gendercoin){
            this.gender = "female";
        }
        else {
            this.gender = "male";
        }

        this.age = makeAge(r);
        this.worker = setWorker(r, age);
        this.student = setStudent(r, age);
        setDap(r);
        setHomeLocation(r);

        // add to MATSim population
        Person p = pf.createPerson(Id.createPersonId(id));
        sc.getPopulation().addPerson(p);
        p.getAttributes().putAttribute("age", age);
        p.getAttributes().putAttribute("gender", gender);
        p.getAttributes().putAttribute("isWorker", worker);
        p.getAttributes().putAttribute("isStudent", student);
        p.getAttributes().putAttribute("typeOfPlan", dap);

        // create a plan for the person
        Plan plan = createPlans(r);
        p.addPlan(plan);
    }

    private Plan createPlans(Random r){
        Plan plan = pf.createPlan();
        Activity homeStart = pf.createActivityFromCoord("home", homeLocation);
        Integer homeStartTime = r.nextInt(3*60*60) + 6*60*60; // have people leave between 6 and 9 am
        plan.addActivity(homeStart);

        //if dap is home, then stay there all day
        if(dap.equals("H")){
            homeStart.setEndTime(homeStartTime);
        }

        // if dap is school, then go to school and maybe go to work
        else if(dap.equals("S")){
            homeStart.setEndTime(homeStartTime);
            plan.addLeg(pf.createLeg("car"));

            Activity school = pf.createActivityFromCoord("school", getRomeLocation("school", r));
            Integer schoolEndTime = r.nextInt(2*60*60) + 14*60*60; //generate random school time between 2:00pm and 4:00pm
            school.setEndTime(schoolEndTime); // make random
            plan.addActivity(school);
            plan.addLeg(pf.createLeg("car"));

            Integer numOfActs = r.nextInt(2);
            if (numOfActs == 0){
                // student goes straight home
                Activity homeEnd = pf.createActivityFromCoord("home", homeLocation);
                plan.addActivity(homeEnd);
            } else {
                // student goes to work before going back to home location
                Activity work = pf.createActivityFromCoord("work", getRomeLocation("work",r));
                Integer workEndTime = r.nextInt(5*60*60) + schoolEndTime; //generate random work activities to end up to 5hrs. after schoolEndTime
                work.setEndTime(workEndTime);
                plan.addActivity(work);
                plan.addLeg(pf.createLeg("car"));

                Activity homeEnd = pf.createActivityFromCoord("home", homeLocation);
                plan.addActivity(homeEnd);
            }
        }

        // if dap Work is mandatory, then go to work and maybe one other place
        else if(dap.equals("W")){
            homeStart.setEndTime(homeStartTime);
            plan.addLeg(pf.createLeg("car"));

            Activity work = pf.createActivityFromCoord("work", getRomeLocation("work", r));
            Integer workEndTime = r.nextInt(3*60*60) + 16*60*60; //generate random work time between 4:00pm and 7:00pm
            work.setEndTime(workEndTime); // make random
            plan.addActivity(work);
            plan.addLeg(pf.createLeg("car"));

            Integer numOfActs = r.nextInt(2);
            if (numOfActs == 0){
                // worker goes straight home
                Activity homeEnd = pf.createActivityFromCoord("home", homeLocation);
                plan.addActivity(homeEnd);
            } else {
                // worker goes to another location before going home location
                Activity other = pf.createActivityFromCoord("other", getRomeLocation("other",r));
                Integer otherEndTime = r.nextInt(4*60*60) + workEndTime; //generate random other activities to end up to 4hrs. after workEndTime
                other.setEndTime(otherEndTime);
                plan.addActivity(other);
                plan.addLeg(pf.createLeg("car"));

                Activity homeEnd = pf.createActivityFromCoord("home", homeLocation);
                plan.addActivity(homeEnd);
            }
        }

        // if dap is non mandatory then go to 1-3 places
        else if(dap.equals("NM")){
            Integer newHomeStart = r.nextInt(6*60*60)+12*60*60; //generate random times for home activities to end between 12pm and 6pm
            homeStart.setEndTime(newHomeStart);
            plan.addLeg(pf.createLeg("car"));

            Activity other1 = pf.createActivityFromCoord("other", getRomeLocation("other",r));
            Integer other1EndTime = r.nextInt(2*3600) + newHomeStart; // generate a random activity up to 2 hrs. after the HomeStart time
            other1.setEndTime(other1EndTime);
            plan.addActivity(other1);
            plan.addLeg(pf.createLeg("car"));

            Integer numOfActs = r.nextInt(3) + 1;
            if (numOfActs == 1) {
                Activity homeEnd = pf.createActivityFromCoord("home", homeLocation);
                plan.addActivity(homeEnd);
            } else{
                Integer timeLeftInDay =  24*60*60 - other1EndTime;
                Activity other2 = pf.createActivityFromCoord("other", getRomeLocation("other",r));
                Integer other2EndTime = r.nextInt(timeLeftInDay - 2*60*60) + other1EndTime; // generate a second other activity
                other2.setEndTime(other2EndTime);
                plan.addActivity(other2);
                plan.addLeg(pf.createLeg("car"));
                if(numOfActs == 3){
                    Integer timeLeftInDay2 = 24*60*60 - other2EndTime;
                    Activity other3 = pf.createActivityFromCoord("other", getRomeLocation("other",r));
                    Integer other3EndTime = r.nextInt(timeLeftInDay2) + other2EndTime; // generate a third other activity
                    other3.setEndTime(other3EndTime);
                    plan.addActivity(other3);
                    plan.addLeg(pf.createLeg("car"));
                }
                Activity homeEnd = pf.createActivityFromCoord("home", homeLocation); // return home
                plan.addActivity(homeEnd);
            }
        }

        return plan;
    }

    private Integer makeAge(Random r){
        Integer top = r.nextInt(64); // people are from 16 to 80 yrs old
        return top + 16;
    }

    private Boolean setStudent(Random r, Integer age) {
        //create code that determines whether someone is a student or non-student (false)
        //could be based on age, gender, + random numbers
        if (age > 25) {
            return false;
        }
        else {
            return r.nextBoolean();
        }
    }

    private Boolean setWorker(Random r, Integer age) {
        // create code that determines whether someone is a worker or non-worker (false)
        // could be based on age, gender, + random numbers
        if (age > 65){
            return false;
        }
        else{
            return r.nextBoolean();
        }
    }

    private void setDap(Random r){
        this.dap = null;
        Double draw = r.nextDouble();

        // for students
        if(student) {
            if (draw < 0.08293) {
                this.dap = "H";
            } else if (draw < 0.08293 + 0.6216277) {
                this.dap = "S";
            } else {
                this.dap = "NM";
            }
        }
        // for workers (non-student)
        else if(worker) {
            if (draw < 0.08293) {
                this.dap = "H";
            } else if (draw < 0.08293 + 0.6216277) {
                this.dap = "W";
            } else {
                this.dap = "NM";
            }
        }
        // for non-workers (non-student)
        else {
            if (draw < 0.2294942) {
                this.dap = "H";
            } else if (draw < 0.2294942 + 0.1654482) {
                this.dap = "W";
            } else {
                this.dap = "NM";
            }
            }
    }

    private Coord randomHomeCord(Random r) {
        float rangeEW = r.nextInt(3793);
        float pointEW=(-1132388+rangeEW)/10000;
        float rangeNS = r.nextInt(1599);
        float pointNS=(376251+rangeNS)/10000;
        Coord homeCord=new Coord(pointEW, pointNS);
        return homeCord;
    }

    // upload Excel file of school locations
    // try to upload coordinates



    private Coord randomWorkCord(Random r) {
        float rangeEW = r.nextInt(11854);
        float pointEW=(12485556+rangeEW)/1000000;
        float rangeNS = r.nextInt(470);
        float pointNS=(4191781+rangeNS)/100000;
        Coord workCord=new Coord(pointEW, pointNS);
        return workCord;
    }

    private Coord randomSchoolCord(Random r) {
        float rangeEW = r.nextInt(11854);
        float pointEW=(12485556+rangeEW)/1000000;
        float rangeNS = r.nextInt(469);
        float pointNS=(4192252+rangeNS)/100000;
        Coord SchoolCord=new Coord(pointEW, pointNS);
        return SchoolCord;
    }

    private Coord randomOtherCord(Random r) {
        float rangeEW = r.nextInt(11855);
        float pointEW=(12473700+rangeEW)/1000000;
        float rangeNS = r.nextInt(469);
        float pointNS=(4192252+rangeNS)/100000;
        Coord OtherCord=new Coord(pointEW, pointNS);
        return OtherCord;
    }



    private void setHomeLocation(Random r){
        Coord homeLatLong = randomHomeCord(r);
        this.homeLocation = ct.transform(homeLatLong);
    }


    private Coord getRomeLocation(String activityType, Random r){
        Coord thisCoord = null;
        if (activityType.equals("school")){
            // get school coordinate
            Coord thisLatLong = randomSchoolCord(r);
            thisCoord = ct.transform(thisLatLong);
        } else if (activityType.equals("work")){
            // get work coordinate in Rome
            Coord thisLatLong = randomWorkCord(r);
            thisCoord = ct.transform(thisLatLong);
        } else {
            // get random other coordinate in Rome
            Coord thisLatLong = randomOtherCord(r);
            thisCoord = ct.transform(thisLatLong);
        }

        return thisCoord;
    }

    public void printInfo(){
        log.info("Person: " + this.id);
        log.info("age: " + this.age);
        log.info("gender: " + this.gender);
    }

}