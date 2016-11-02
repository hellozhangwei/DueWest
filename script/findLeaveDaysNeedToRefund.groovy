

/*
def leaveDays = [new Date().parse("yyyy-MM-dd", '2016-10-31')
             , new Date().parse("yyyy-MM-dd", '2016-11-01')
             , new Date().parse("yyyy-MM-dd", '2016-11-02')
             , new Date().parse("yyyy-MM-dd", '2016-11-04')
             , new Date().parse("yyyy-MM-dd", '2016-11-07')
             , new Date().parse("yyyy-MM-dd", '2016-11-08')
             , new Date().parse("yyyy-MM-dd", '2016-11-09')
             , new Date().parse("yyyy-MM-dd", '2016-11-11')
]
*/
context.workingDays = getWorkingDays()

context.workingDays.each{workingDay->

    println workingDay.format('yyyy-MM-dd')
}

findLeaveDays()
println "========context.leaveDays=${context.leaveDays}==========="

findLeaveDaysInWorkingDays(context.leaveDays)

findLeaveDaysNeedToRefund()

def findLeaveDaysNeedToRefund() {
    def leaveDaysNeedToRefund = []
    context.indexGroups.each {indexGroup->
        println "=========indexGroup=====${indexGroup.size()}=====${indexGroup}================"
        if(indexGroup.size()>=3) {

            indexGroup.each{index->
                leaveDaysNeedToRefund.add([leaveDate:context.workingDays[index]])
            }
        }

    }

    context.leaveDaysNeedToRefund = leaveDaysNeedToRefund
}

def findLeaveDays() {
    EntityList leaveRequestList = ec.entity.find("mantle.humanres.employment.LeaveRequest").orderBy('fromDate').list()
    context.leaveRequestList = leaveRequestList
    def leaveDays = []
    leaveRequestList.each { leave ->
        GregorianCalendar cal = new GregorianCalendar()
        GregorianCalendar calStart = new GregorianCalendar()
        calStart.setTime(leave.fromDate.clearTime())
        cal.setTime(leave.fromDate.clearTime())

        GregorianCalendar calEnd = new GregorianCalendar()
        calEnd.setTime(leave.thruDate.clearTime())

        for (int i = calStart.get(Calendar.DAY_OF_YEAR); i <= calEnd.get(Calendar.DAY_OF_YEAR); i++) {
            cal.set(Calendar.DAY_OF_YEAR, i)
            println "=========cal.getTime()=${cal.getTime()}======="
            def leaveDay = [:]
            leaveDay.leaveDate = cal.getTime()
            leaveDays.add(leaveDay)
        }

        println "======41===leaveDays=${leaveDays}======="

        //remove holiday in leaveDays

        leaveDays.removeIf{isHoliday(it.leaveDate)}
        context.leaveDays = leaveDays

    }
}

def findLeaveDaysInWorkingDays(leaveDays) {
    def workingDays = context.workingDays

    def foundIndices = []

    def nextFoundPosition = -1
    for (int i=0;i<leaveDays.size();i++) {

        for (int j = nextFoundPosition +1 ; j < workingDays.size(); j++) {
            if (leaveDays[i].leaveDate.clearTime().getTime() == workingDays[j].clearTime().getTime()) {
                nextFoundPosition = j
                foundIndices.add(nextFoundPosition)
                //println "======nextFoundPosition=${nextFoundPosition}==${workingDays[j]}============"
                break
            }
        }

    }

    println "=========foundIndices==${foundIndices}======="
    breakFoundWorkingDays(foundIndices)

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
    context.indexGroups = indexGroups
   /* indexGroups.each {indexGroup->
        println "=========indexGroup=${indexGroup}================"
        indexGroup.each{index->

        }

    }*/


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
    workingDays = workingDays.toSorted()
    return workingDays
}

def addWeekendWorkingDays(workingDays) {
    if(!workingDays) {
        return
    }

    EntityList weekendWorkingDays = ec.entity.find("mantle.work.effort.WorkEffort").selectField("estimatedStartDate").condition([workEffortTypeEnumId:'BusinessWeekend']).list()

    weekendWorkingDays.each { weekendWorkingDay->
        println "weekendWorkingDay====================${weekendWorkingDay.estimatedStartDate.format('yyyy-MM-dd')}======="

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
