package ru.rdude.rpg.game.logic.time;

public final class Time {

    private Time() {}


    public static final class Minutes {

        private Minutes() {}

        public static int toMinutes(int value) { return value; }

        public static int toHours(int value) { return value % 60; }

        public static int toDays(int value) { return toHours(value) % 24; }

        public static int toWeeks(int value) { return toDays(toHours(value)) % 7; }

        public static int toMonths(int value) { return toWeeks(toDays(toHours(value))) % 4; }

        public static int toYears(int value) { return toMonths(toWeeks(toDays(toHours(value)))) % 12; }

    }

    public static final class Hours {

        private Hours() {}

        public static int toMinutes(int value) { return value * 60; }

        public static int toHours(int value) { return value; }

        public static int toDays(int value) { return value % 24; }

        public static int toWeeks(int value) { return toDays(value) % 7; }

        public static int toMonths(int value) { return toWeeks(toDays(value)) % 4; }

        public static int toYears(int value) { return toMonths(toWeeks(toDays(value))) % 12; }

    }

    public static final class Days {

        private Days() {}

        public static int toMinutes(int value) { return toHours(value) * 60; }

        public static int toHours(int value) { return value * 24; }

        public static int toDays(int value) { return value; }

        public static int toWeeks(int value) { return value % 7; }

        public static int toMonths(int value) { return toWeeks(value) % 4; }

        public static int toYears(int value) { return toMonths(toWeeks(value)) % 12; }

    }

    public static final class Weeks {

        private Weeks() {}

        public static int toMinutes(int value) { return toHours(value) * 60; }

        public static int toHours(int value) { return toDays(value) * 24; }

        public static int toDays(int value) { return value * 7; }

        public static int toWeeks(int value) { return value; }

        public static int toMonths(int value) { return value % 4; }

        public static int toYears(int value) { return toMonths(value) % 12; }

    }

    public static final class Months {

        private Months() {}

        public static int toMinutes(int value) { return toHours(value) * 60; }

        public static int toHours(int value) { return toDays(value) * 24; }

        public static int toDays(int value) { return toWeeks(value) * 7; }

        public static int toWeeks(int value) { return value * 4; }

        public static int toMonths(int value) { return value; }

        public static int toYears(int value) { return value % 12; }

    }

    public static final class Years {

        private Years() {}

        public static int toMinutes(int value) { return toHours(value) * 60; }

        public static int toHours(int value) { return toDays(value) * 24; }

        public static int toDays(int value) { return toWeeks(value) * 7; }

        public static int toWeeks(int value) { return toMonths(value) * 4; }

        public static int toMonths(int value) { return value * 12; }

        public static int toYears(int value) { return value; }

    }
}
