package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;

/**
 * Validates {@code stops.stop_lat} and {@code stops.stop_long} exist for a single {@code GtfsStop}
 * of type Stop, Station, Entrance, and Exit.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopWithoutLocationNotice}
 * </ul>
 */
@GtfsValidator
public class StopRequiredLocationValidator extends SingleEntityValidator<GtfsStop> {

  @Override
  public void validate(GtfsStop stop, NoticeContainer noticeContainer) {
    if ((stop.locationType() == GtfsLocationType.STOP
            || stop.locationType() == GtfsLocationType.STATION
            || stop.locationType() == GtfsLocationType.ENTRANCE)
        && !stop.hasStopLatLon()) {
      noticeContainer.addValidationNotice(
          new StopWithoutLocationNotice(stop.csvRowNumber(), stop.stopId(), stop.locationType()));
    }
  }

  /**
   * `stop_lat` and/or `stop_lon` is missing for stop with `location_type` equal to`0`, `1`, or `2`
   *
   * <p>`stop_lat` and/or `stop_lon` are required for locations that are stops (`location_type=0`),
   * stations (`location_type=1`) or entrances/exits (`location_type=2`).
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsStopSchema.class))
  static class StopWithoutLocationNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's `stops.location_type`. */
    private final GtfsLocationType locationType;

    /** The faulty record's id. */
    private final String stopId;

    StopWithoutLocationNotice(int csvRowNumber, String stopId, GtfsLocationType type) {
      this.stopId = stopId;
      this.csvRowNumber = csvRowNumber;
      this.locationType = type;
    }
  }
}
