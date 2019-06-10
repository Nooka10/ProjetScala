import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const dayTranslation = {
  MONDAY: 'Lundi',
  TUESDAY: 'Mardi',
  WEDNESDAY: 'Mercredi',
  THURSDAY: 'Jeudi',
  FRIDAY: 'Vendredi',
  SATURDAY: 'Samedi',
  SUNDAY: 'Dimanche',
};

export default function DaySchedule(props) {
  const { schedule } = props;

  return (
    <View style={styles.scheduleView}>
      <Text style={styles.scheduleDay}>{dayTranslation[schedule.day]}</Text>
      <Text style={styles.scheduleHours}>{`${schedule.hOpenAM} - `}</Text>
      {schedule.hCloseAM
        ? (
          <View style={styles.scheduleView}>
            <Text>{schedule.hCloseAM}</Text>
            <Text style={styles.scheduleHours}>{`${schedule.hOpenPM} - ${schedule.hClosePM}`}</Text>
          </View>
        )
        : (<Text>{schedule.hClosePM}</Text>)}
    </View>
  );
}

const styles = StyleSheet.create({
  scheduleView: {
    flex: 1,
    flexDirection: 'row',
  },
  scheduleDay: {
    width: 60,
  },
  scheduleHours: {
    marginLeft: 20,
  }
});
