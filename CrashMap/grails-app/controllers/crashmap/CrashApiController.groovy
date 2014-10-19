package crashmap

import grails.rest.RestfulController

class CrashApiController extends RestfulController {
    static responseFormats = ['json']
	def myFormat = 'MM-dd-yyyy'
	//def inputFile = 
	
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

					if (!crash.save(flush: true)) {
						crash.errors.each {
							println it
						}
					}
				}
			}
		
		render(contentType:"text/json") {
			success: 'true'
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

		def crashes = CrashService.getCrashesNearPointAndBetweenDates(params.latitude, params.longitude, params.fromDate, params.toDate)
		
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
		
		def countByYear = CrashService.getCrashCountByPoint(params.latitude, params.longitude);
		
		render(contentType:"text/json") {
			countByYear
		}
	}
	
	
}
