# Java Course Repository

## Domains

### 1. Library
- **Book**: title, authors, isbn
- **Author**: firstName, lastName, birthYear
- **Reader**: firstName, lastName, readerId
- **Loan**: book, reader, issueDate, returnDate
- **Membership**: reader, startDate, endDate

**For the second lab:** Convert Author and Reader to `record`. Add enums:
- `BookStatus { AVAILABLE, CHECKED_OUT, RESERVED, LOST }`
- `MembershipType { STANDARD, PREMIUM, STUDENT, SENIOR }`

### 2. Electronics Store
- **Product**: name, category, price, stock
- **Category**: name, description
- **Supplier**: name, contactInfo
- **Customer**: firstName, lastName, email
- **Order**: customer, products, orderDate

**For the second lab:** Convert Category, Supplier, and Customer to `record`. Add enums:
- `ProductStatus { IN_STOCK, OUT_OF_STOCK, DISCONTINUED }`
- `OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELED }`

### 3. Online Courses
- **Course**: title, description, credits, startDate
- **Instructor**: firstName, lastName, expertise
- **Student**: firstName, lastName, email, enrollmentDate
- **Module**: title, content
- **Assignment**: module, dueDate, maxPoints

**For the second lab:** Convert Instructor, Student, and Module to `record`. Add enums:
- `CourseLevel { BEGINNER, INTERMEDIATE, ADVANCED }`
- `AssignmentType { HOMEWORK, PROJECT, QUIZ, EXAM }`

### 4. Sports Team
- **Player**: firstName, lastName, position, number
- **Coach**: firstName, lastName, role
- **Team**: name, sport, players
- **Match**: homeTeam, awayTeam, matchDateTime, score
- **TrainingSession**: sessionDateTime, duration, team

**For the second lab:** Convert Coach and Team to `record`. Add enums:
- `PlayerPosition { GOALKEEPER, DEFENDER, MIDFIELDER, FORWARD }`
- `CoachRole { HEAD_COACH, ASSISTANT_COACH, FITNESS_COACH, GOALKEEPING_COACH }`

### 5. Medical Center
- **Patient**: firstName, lastName, patientId, birthDate
- **Doctor**: firstName, lastName, specialty
- **Appointment**: patient, doctor, appointmentDateTime
- **Medication**: name, dosage, instructions
- **MedicalRecord**: patient, diagnosis, treatments, recordDateTime

**For the second lab:** Convert Patient and Doctor to `record`. Add enums:
- `Specialty { GENERAL_PRACTITIONER, CARDIOLOGIST, DERMATOLOGIST, PEDIATRICIAN, NEUROLOGIST }`
- `MedicationType { TABLET, SYRUP, INJECTION, OINTMENT }`
- `AppointmentStatus { SCHEDULED, COMPLETED, CANCELED, NO_SHOW }`

### 6. Car Rental
- **Car**: licensePlate, model, year, mileage
- **Customer**: firstName, lastName, driverLicense, birthDate
- **Rental**: car, customer, startDate, endDate
- **Payment**: rental, amount, paymentDate
- **Branch**: name, location

**For the second lab:** Convert Customer and Branch to `record`. Add enums:
- `CarStatus { AVAILABLE, RENTED, MAINTENANCE, RESERVED }`
- `PaymentMethod { CREDIT_CARD, DEBIT_CARD, CASH, ONLINE }`

### 7. Hotel Management
- **Room**: roomNumber, type, capacity, price
- **Guest**: firstName, lastName, email, checkInDate
- **Reservation**: guest, room, startDate, endDate
- **Service**: name, price
- **Invoice**: reservation, totalAmount, issueDate

**For the second lab:** Convert Guest and Service to `record`. Add enums:
- `RoomStatus { AVAILABLE, OCCUPIED, CLEANING, MAINTENANCE }`
- `ReservationStatus { CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELED }`

### 8. Cinema
- **Movie**: title, genre, durationMinutes, releaseDate
- **Actor**: firstName, lastName, birthYear
- **Hall**: hallNumber, capacity
- **Screening**: movie, hall, screeningDateTime
- **Ticket**: screening, seatNumber, price

**For the second lab:** Convert Actor and Hall to `record`. Add enums:
- `TicketStatus { AVAILABLE, RESERVED, SOLD, CANCELED }`
- `Genre { ACTION, DRAMA, COMEDY, HORROR, DOCUMENTARY }`

### 9. E-commerce
- **Product**: name, price, stock, createdDate
- **Customer**: firstName, lastName, email, registrationDate
- **Order**: customer, products, orderDate, totalAmount
- **Review**: product, customer, rating, comment, reviewDate
- **Shipment**: order, shipmentDate, trackingNumber

**For the second lab:** Convert Customer and Review to `record`. Add enums:
- `OrderStatus { NEW, PROCESSING, SHIPPED, DELIVERED, RETURNED }`
- `PaymentStatus { PENDING, PAID, FAILED, REFUNDED }`

### 10. Social Network
- **User**: username, email, registrationDate
- **Post**: user, content, postDate
- **Comment**: post, user, content, commentDate
- **FriendRequest**: sender, receiver, requestDate, status
- **Message**: sender, receiver, content, sentDate

**For the second lab:** Convert User and Comment to `record`. Add enums:
- `FriendRequestStatus { PENDING, ACCEPTED, DECLINED, CANCELED }`
- `MessageStatus { SENT, DELIVERED, READ }`

### 11. Banking System
- **Account**: accountNumber, owner, balance
- **Customer**: firstName, lastName, email
- **Transaction**: fromAccount, toAccount, amount, date
- **Loan**: customer, amount, interestRate, startDate
- **Branch**: name, location

**For the second lab:** Convert Customer and Branch to `record`. Add enums:
- `AccountType { CHECKING, SAVINGS, CREDIT }`
- `TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT }`

### 12. Food Delivery
- **Restaurant**: name, cuisine, location
- **MenuItem**: name, price, category
- **Customer**: firstName, lastName, address
- **Order**: customer, items, orderDate
- **Delivery**: order, deliveryPerson, deliveryTime

**For the second lab:** Convert Customer and Delivery to `record`. Add enums:
- `OrderStatus { PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELED }`
- `CuisineType { ITALIAN, CHINESE, MEXICAN, AMERICAN, INDIAN }`

### 13. Airline
- **Flight**: flightNumber, origin, destination, departureTime, arrivalTime
- **Passenger**: firstName, lastName, passportNumber
- **CrewMember**: firstName, lastName, role
- **Booking**: passenger, flight, seatNumber
- **Airport**: name, location

**For the second lab:** Convert Passenger and CrewMember to `record`. Add enums:
- `FlightStatus { ON_TIME, DELAYED, CANCELED, BOARDING }`
- `CrewRole { PILOT, CO_PILOT, FLIGHT_ATTENDANT }`

### 14. Library 2 (Digital)
- **EBook**: title, authors, isbn, files
- **Author**: firstName, lastName, birthYear
- **Reader**: firstName, lastName, readerId
- **File**: url, format, size, type
- **LibraryAccess**: reader, ebook, accessStartDate, accessEndDate

**For the second lab:** Convert Author, Reader and File to `record`. Add enums:
- `EBookFormat { PDF, EPUB, MOBI }`
- `EBookType { PUBLIC, RESTRICTED }`

### 15. Fitness App
- **User**: firstName, lastName, email
- **Workout**: title, durationMinutes, intensity
- **Exercise**: name, reps, sets
- **Plan**: user, workouts, startDate
- **Progress**: user, date, weight, bmi

**For the second lab:** Convert User and Exercise to `record`. Add enums:
- `WorkoutIntensity { LOW, MEDIUM, HIGH }`
- `PlanType { BEGINNER, INTERMEDIATE, ADVANCED }`

### 16. Movie Streaming
- **Movie**: title, genre, releaseDate, durationMinutes
- **User**: firstName, lastName, email
- **Subscription**: user, startDate, endDate
- **Playlist**: user, movies
- **Review**: movie, user, rating, comment

**For the second lab:** Convert User and Review to `record`. Add enums:
- `SubscriptionType { BASIC, STANDARD, PREMIUM }`
- `Genre { ACTION, DRAMA, COMEDY, HORROR, DOCUMENTARY }`

### 17. Real Estate
- **Property**: address, type, price, area
- **Agent**: firstName, lastName, contactInfo
- **Client**: firstName, lastName, email
- **Sale**: property, agent, client, saleDate
- **Visit**: property, client, visitDate

**For the second lab:** Convert Agent and Client to `record`. Add enums:
- `PropertyType { APARTMENT, HOUSE, OFFICE, LAND }`
- `SaleStatus { PENDING, COMPLETED, CANCELED }`

### 18. University
- **Student**: firstName, lastName, studentId, enrollmentDate
- **Professor**: firstName, lastName, department
- **Course**: title, credits, semester
- **Enrollment**: student, course
- **Exam**: course, date, maxPoints

**For the second lab:** Convert Student and Professor to `record`. Add enums:
- `Department { MATH, PHYSICS, CHEMISTRY, COMPUTER_SCIENCE, BIOLOGY }`
- `ExamType { MIDTERM, FINAL, QUIZ, PROJECT }`

### 19. Transport Logistics
- **Vehicle**: licensePlate, model, capacity
- **Driver**: firstName, lastName, licenseNumber
- **Route**: origin, destination, distance
- **Shipment**: vehicle, route, cargo, shipmentDate
- **Warehouse**: name, location

**For the second lab:** Convert Driver and Warehouse to `record`. Add enums:
- `VehicleType { TRUCK, VAN, MOTORCYCLE }`
- `ShipmentStatus { PENDING, IN_TRANSIT, DELIVERED, CANCELED }`

### 20. Retail Store
- **Item**: name, price, stock
- **Category**: name, description
- **Customer**: firstName, lastName, email
- **Purchase**: customer, items, purchaseDate
- **Supplier**: name, contactInfo

**For the second lab:** Convert Customer and Supplier to `record`. Add enums:
- `ItemStatus { IN_STOCK, OUT_OF_STOCK, DISCONTINUED }`
- `PurchaseStatus { PENDING, COMPLETED, RETURNED }`  
