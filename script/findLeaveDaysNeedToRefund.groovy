

def leaveDays = [new Date().parse("yyyy-MM-dd", '2016-10-31')
             , new Date().parse("yyyy-MM-dd", '2016-11-01')
             , new Date().parse("yyyy-MM-dd", '2016-11-02')
             , new Date().parse("yyyy-MM-dd", '2016-11-04')
             , new Date().parse("yyyy-MM-dd", '2016-11-07')
             , new Date().parse("yyyy-MM-dd", '2016-11-08')
             , new Date().parse("yyyy-MM-dd", '2016-11-09')
             , new Date().parse("yyyy-MM-dd", '2016-11-11')
]



findLeaveDaysInWorkingDays(leaveDays)


def findLeaveDaysInWorkingDays(leaveDays) {
    def workingDays = getWorkingDays()

    def foundIndex = []

    def nextFoundPosition = -1
    for (int i=0;i<leaveDays.size();i++) {

        for (int j = nextFoundPosition +1 ; j < workingDays.size(); j++) {
            if (leaveDays[i].clearTime().getTime() == workingDays[j].clearTime().getTime()) {
                nextFoundPosition = j
                foundIndex.add(nextFoundPosition)
                println "======nextFoundPosition=${nextFoundPosition}==${workingDays[j]}============"
                break
            }
        }

    }

    println "=========foundIndex==${foundIndices}======="
    breakFoundWorkingDays(foundIndex)

}

def breakFoundWorkingDays(foundIndices) {

    def indexGroups = []

    if(!foundIndices) {
        return indexGroups
    }

    def temp = []

    indexGroups.add(temp)
    for (int i = 1; i < foundIndices.size; i++)
    {
        int pre = foundIndices[i - 1]
        temp.add(pre)

        if (foundIndices[i] - pre > 1)
        {
            temp = [];
            indexGroups.add(temp)
        }
    }

    temp.add(foundIndices.last())

    indexGroups.each {indexGroup->
        println "=========indexGroup=${indexGroup}================"
    }
}


def getWorkingDays() {

    List<Date> workingDays = new ArrayList();

    GregorianCalendar cal=new GregorianCalendar();

    int year=2016;
    int total=365;

    if (cal.isLeapYear(year)) {
        total++;
    }

    for(int d=1; d<=total; d++) {
        cal.set(Calendar.DAY_OF_YEAR, d);
        Date date = cal.getTime();
        int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);

        if((dayOfTheWeek != Calendar.SATURDAY) && (dayOfTheWeek != Calendar.SUNDAY)) {
            workingDays.add(date);
        }

    }


    workingDays.removeIf {isHoliday(it) }
    addWeekendWorkingDays(workingDays)

    return workingDays
}

def addWeekendWorkingDays(workingDays) {
    if(!workingDays) {
        return
    }

    EntityList weekendWorkingDays = ec.entity.find("mantle.work.effort.WorkEffort").selectField("estimatedStartDate").condition([workEffortTypeEnumId:'BusinessWeekend']).list()
    weekendWorkingDays.each { weekendWorkingDay->
        workingDays.addAll(new Date(weekendWorkingDay.estimatedStartDate.getTime()))
    }

}

def isHoliday(Date date) {

    EntityList publicHolidays = ec.entity.find("mantle.work.effort.WorkEffort").selectField("estimatedStartDate").condition([workEffortTypeEnumId:'PublicHoliday']).list()

    for (int i=0;i<publicHolidays.size();i++) {

        if(publicHolidays[i].estimatedStartDate.clearTime().getTime() == date.clearTime().getTime()) {
            return true;
        }
    }
    return false;
}
