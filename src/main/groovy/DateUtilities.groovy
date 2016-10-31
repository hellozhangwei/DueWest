
class DateUtilities {

    static List<Date> getWorkingDays(List<Date> publicHolidays) {
        return getWorkingDays(null, null, publicHolidays)
    }



    static List<Date> getWorkingDays(Date startDate, Date endDate, List<Date> publicHolidays) {
        /*Calendar startCal;
        Calendar endCal;
        startCal = Calendar.getInstance();

        if(startDate) {
            startCal.setTime(startDate);
        } else {
            startCal.setTime(new Date(2016-1900, 0, 1))
        }

        endCal = Calendar.getInstance();
        if(endDate) {
            endCal.setTime(endDate);
        } else {
            endCal.setTime(new Date(2016-1900, 11, 31))
        }

*/

        List<Date> workingDays = new ArrayList();

        GregorianCalendar cal=new GregorianCalendar();
        int year=2016;
        int total=365;
        cal.set(Calendar.YEAR, year);
        if (cal.isLeapYear(year)) {
            total++;
        }

        for(int d=1; d<=total; d++) {
            cal.set(Calendar.DAY_OF_YEAR, d);
            Date date = cal.getTime();

            int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);

            if((dayOfTheWeek != Calendar.SATURDAY) && (dayOfTheWeek == Calendar.SUNDAY)) {
                workingDays.add(date);
            }

        }


//        def yearStart = new Date().clearTime()
//        yearStart.set(year: 2016, month: NOVEMBER, date: 1)

//        def yearEnd = new Date().clearTime()
//        yearStart.set(year: 2016, month: NOVEMBER, date: 31)

//        def today = new Date().clearTime()
//        def nextWeek = today + 7

//        yearStart.upto(yearEnd) {
            // Print day of the week.
//            workingDays.add(it.copy());
//        }

        return workingDays
    }

    static boolean isHoliday(Date date, List<Date> publicHolidays) {
        if(!publicHolidays || !date) {
            return false
        }

        for(publicHoliday in publicHolidays) {
            if(publicHoliday.equals(date)) {
                return true
            }
        }

        return false

    }


}