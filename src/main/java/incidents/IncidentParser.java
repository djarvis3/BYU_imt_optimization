package incidents;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class IncidentParser {
	public static List<Incident> parse(String csv) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		List<Record> records = parser.parseAllRecords(new File(csv));

		return records.stream()
				.map(record -> {
					String linkId = record.getString("MATSim Link");
					int incidentId = record.getInt("ID");
					int respondingIMTs = record.getInt("Responding IMTs");
					double start = record.getDouble("Start Time (sec)");
					double end = record.getDouble("End Time (sec)");
					double reduction = record.getDouble("Capacity reduction (w/ TWA)");
					return new Incident(linkId, incidentId, respondingIMTs, start, end, reduction);
				})
				.collect(Collectors.toList());
	}
}
