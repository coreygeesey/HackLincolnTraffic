package crashmap

import grails.rest.RestfulController

class CrashApiController extends RestfulController {
    static responseFormats = ['json']
	def myFormat = 'MM-dd-yyyy'
	
	def addCrashData() {
		// Grab the data from csv
		/*
		 * Need indexes:
			2	5	31	37	38
		 */
		InputStream csvCrashData = this.class.classLoader.getResourceAsStream('data/crashData2014.csv');
		csvCrashData.eachCsvLine { line ->
			def hasError = false;
				Date date;
				try {
					date = new Date().parse(myFormat, line[5]);
				} catch (err) {
					hasError = true;
					println("ERROR: Date was not parsible");
				}
				
				def address = line[2];
				def score = line[31];
				def latitude = line[38];
				def longitude = line[37];
				
				if (!score.isNumber() || !latitude.isNumber() || !longitude.isNumber()) {
					hasError = true;
				}
				
				if (hasError == false) {
					def crash = new Crash(
						address: address,
						date: date,
						score: score,
						latitude: latitude,
						longitude: longitude);

					println(date);
					if (!crash.save(flush: true)) {
						crash.errors.each {
							println it
						}
					}
				} else {
					println("Could not add this row");
				}
			}
		
		render(contentType:"text/json") {
			success: true
		}
	}
	
	def crashData() {
		def errorList = [];
		
		// Check if they passed the parameters
		if (!params.fromDate) {
			errorList.add("ERROR: 'fromDate' parameter missing from request");
			println("ERROR: 'fromDate' parameter missing from request");
		}
		if (!params.toDate) {
			errorList.add("ERROR: 'toDate' parameter missing from request");
			println("ERROR: 'toDate' parameter missing from request");
		}
		
		// Send errorList to gsp at this point if there are any errors
		if (errorList.size() > 0) {
			[errorList: errorList]
			return;
		}
		
		//def myFormat = 'MM-dd-yyyy'
		Date toDate = new Date().parse(myFormat, params.toDate);
		Date fromDate = new Date().parse(myFormat, params.fromDate);
		
		// Query the DB and return as JSON
		def crashes = Crash.findAll {
			date >= fromDate && date <= toDate
		}
		
		render(contentType:"text/json") {
			crashes
		}
	}
	
	def allData() {
		def crashes = Crash.getAll();
		render(contentType:"text/json") {
			crashes
		}
	}
	
	def crashDataNearPoint() {
		def errorList = [];
		
		// Check if they passed the parameters
		if (!params.latitude || !params.latitude.isNumber()) {
			errorList.add("ERROR: 'latitude' parameter missing from request");
			println("ERROR: 'latitude' parameter missing from request");
		}
		if (!params.longitude || !params.latitude.isNumber()) {
			errorList.add("ERROR: 'longitude' parameter missing from request");
			println("ERROR: 'longitude' parameter missing from request");
		}

		if (!params.fromDate) {
			errorList.add("ERROR: 'fromDate' parameter missing from request");
			println("ERROR: 'fromDate' parameter missing from request");
		}
		if (!params.toDate) {
			errorList.add("ERROR: 'toDate' parameter missing from request");
			println("ERROR: 'toDate' parameter missing from request");
		}
		
		// Send errorList to gsp at this point if there are any errors
		if (errorList.size() > 0) {
			[errorList: errorList]
			return;
		}

		Date toDate = new Date().parse(myFormat, params.toDate);
		Date fromDate = new Date().parse(myFormat, params.fromDate);

		def lat = params.latitude as BigDecimal;
		def lon = params.longitude as BigDecimal;
		
		def upperLat = lat + 00.00003
		def lowerLat = lat - 00.00003
		def upperLong = lon + 00.00003
		def lowerLong = lon - 00.00003
		
		println(upperLat);
		println(lowerLat);
		println(upperLong);
		println(lowerLong);
		
		// Grab the data by latitude and longitude
		// Query the DB and return as JSON
		def crashes = Crash.findAll {
			latitude >= lowerLat && latitude <= upperLat && longitude >= lowerLong && longitude <= upperLong && date >= fromDate && date <= toDate
		}
		
		render(contentType:"text/json") {
			crashes
		}
	}
	
	def crashCountNearPoint() {
		def errorList = [];
		
		// Check if they passed the parameters
		if (!params.latitude || !params.latitude.isNumber()) {
			errorList.add("ERROR: 'latitude' parameter missing from request");
			println("ERROR: 'latitude' parameter missing from request");
		}
		if (!params.longitude || !params.latitude.isNumber()) {
			errorList.add("ERROR: 'longitude' parameter missing from request");
			println("ERROR: 'longitude' parameter missing from request");
		}
		
		// Send errorList to gsp at this point if there are any errors
		if (errorList.size() > 0) {
			[errorList: errorList]
			return;
		}
		
		def lat = params.latitude as BigDecimal;
		def lon = params.longitude as BigDecimal;
		
		def upperLat = lat + 00.00003
		def lowerLat = lat - 00.00003
		def upperLong = lon + 00.00003
		def lowerLong = lon - 00.00003
		
		def returnData = []
		
		def yearList = [2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014]
		for (year in yearList) {
			Date fromDate = new Date().parse(myFormat, "1-1-"+year)
			Date toDate = new Date().parse(myFormat, "12-31-"+year)
			println(fromDate.toLocaleString());
			println(toDate.toLocaleString());
			
			def crashes = Crash.findAll {
				latitude >= lowerLat && latitude <= upperLat && longitude >= lowerLong && longitude <= upperLong && date >= fromDate && date <= toDate
			}
			
			def crashCount = crashes.size();
			
			CrashCount tempObj = new CrashCount(
				year: year,
				crashCount: crashCount
			)
			
			// add crashes to 
			returnData.add(tempObj);
		}
		
		render(contentType:"text/json") {
			returnData
		}
	}
}
