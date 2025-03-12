# Currency Exchange REST API

## Description
This project implements a REST API for managing currencies and exchange rates. It allows clients to:
- Retrieve, add, and update currencies.
- Retrieve, add, and update exchange rates.
- Calculate currency conversion for a given amount.

The API follows a layered architecture:
- **Backend**: Java servlets with JDBC for persistence, implementing business logic.
- **Frontend**: A simple static web interface served by Nginx.
- **Docker**: Containers for PostgreSQL, Tomcat, and Nginx are used for deployment.

---

## Project Structure
```
CurrencyExchange/
├── frontend/                       # Frontend code (HTML, CSS, JS)
├── backend/
│   ├── pom.xml                     # Maven configuration for backend
│   ├── src/main/java/org/kivislime/currencyexchange/
│   │   ├── controller/             # HTTP servlets for REST API
│   │   ├── exception/              # Custom exception classes
│   │   ├── model/                  # Domain models, DAO interfaces & DTOs
│   │   ├── service/                # Business logic
│   │   └── util/                   # Utility classes (JSON, parsing, etc.)
│   └── src/main/resources/webapp/  # Web configuration
│       └── WEB-INF/web.xml
├── docker-compose.yml               # Docker compose file for container orchestration
└── build.sh                         # Script to build and deploy the application
```

---

## Technology Stack
- **Java 11+** – Servlets, JDBC, OOP, Collections.
- **PostgreSQL** – Data storage.
- **Maven** – Build tool.
- **Docker** – Containerization.
- **Tomcat 10** – Application server.
- **Nginx** – Static file server for the frontend.

---

## API Endpoints

All endpoints return JSON. HTTP status codes follow the standard conventions.

**Note:**  
To send requests, use the address:  
**http://localhost:8080/currency-exchange/**  
For example, to get a list of currencies:  
**GET http://localhost:8080/currency-exchange/currencies**

Or just open **http://localhost/**, where the frontend is located,
and use the application interface.

### **Currencies**

#### GET /currencies    
- **Description:** Retrieve a list of all currencies.
- **Example Response:**
```
  [
      {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },
      {
          "id": 1,
          "name": "Euro",
          "code": "EUR",
          "sign": "€"
      }
  ]
```
HTTP Codes: 200 (Success), 500 (Server error)

#### GET /currency/{code}
- **Description:** Retrieve a specific currency by its code.
- **Example Response:**
```
{
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```
HTTP Codes: 200 (Success), 400 (Missing code), 404 (Not found), 500 (Server error)

#### POST /currencies
- **Description:** Add a new currency. Data is passed in the request body using application/x-www-form-urlencoded.
- **Required Form Fields:** name, code, sign
- **Example Request Body:**
```
name=Euro&code=EUR&sign=€
```
- **Example Response:**
```
{
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```
HTTP Codes: 201 (Created), 400 (Missing field), 409 (Currency exists), 500 (Server error)

### **Exchange Rates**

#### GET /exchange-rates
- **Description:** Retrieve a list of all exchange rates.
- **Example Response:**
```
[
    {
        "id": 0,
        "baseCurrency": {
            "id": 0,
            "name": "United States dollar",
            "code": "USD",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Euro",
            "code": "EUR",
            "sign": "€"
        },
        "rate": 0.99
    }
]
```
HTTP Codes: 200 (Success), 500 (Server error)

#### GET /exchange-rate/{baseCurrencyCode}{targetCurrencyCode}
- **Description:** Retrieve the exchange rate for a currency pair.
- **Note:** The pair is specified as two currency codes not separated by a hyphen (e.g., USDEUR).
- **Example Response:**
```
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
}

```
HTTP Codes: 200 (Success), 400 (Invalid pair), 404 (Not found), 500 (Server error)

#### POST /exchangeRates
- **Description:** Add a new exchange rate. Data is passed in the request body using application/x-www-form-urlencoded.
- **Required Form Fields:** baseCurrencyCode, targetCurrencyCode, rate
- **Example Request Body:**
```
baseCurrencyCode=USD&targetCurrencyCode=EUR&rate=0.99
```
- **Example Response:**
```
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```
HTTP Codes: 201 (Created), 400 (Missing field), 409 (Exchange rate exists), 404 (One or both currencies not found), 500 (Server error)

#### PATCH /exchange-rate/{baseCurrencyCode}{targetCurrencyCode}
- **Description:** Update an existing exchange rate. Data is passed in the request body using application/x-www-form-urlencoded.
- **Required Form Field:** rate
- **Example Request Body:**
```
rate=80
```
- **Example Response:**
```
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
}
```
HTTP Codes: 200 (Success), 400 (Missing field), 404 (Exchange rate not found), 500 (Server error)

### **Currency Conversion**

#### GET /exchange?from={fromCurrencyCode}&to={toCurrencyCode}&amount={amount}
- **Description:** Converts a specified amount from one currency to another.
- **Example Request:**
```
GET /exchange?from=USD&to=AUD&amount=10
```
- **Example Response:**
```
{
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A$"
    },
    "rate": 1.45,
    "amount": 10.00,
    "convertedAmount": 14.50
}
```
HTTP Codes: 200 (Success), 400 (Missing/invalid parameter), 404 (Exchange rate not found), 500 (Server error)

### **Database Schema**

#### Currencies Table
| Column    | Type      | Description                                 |
|-----------|-----------|---------------------------------------------|
| id        | int       | Unique identifier (auto-increment)          |
| code      | varchar   | Currency code (e.g., USD, EUR)            |
| full_name | varchar   | Full name of the currency                   |
| sign      | varchar   | Currency symbol (e.g., $, €, ₽)             |

#### ExchangeRates Table
| Column             | Type        | Description                                                               |
|--------------------|-------------|---------------------------------------------------------------------------|
| id                 | int         | Unique identifier (auto-increment)                                        |
| base_currency_id   | int         | Foreign key referencing Currencies (base currency)                         |
| target_currency_id | int         | Foreign key referencing Currencies (target currency)                       |
| rate               | decimal(6,4) | Exchange rate for the currency pair (precision and scale may vary)        |

### **Build and Deploy**

#### Building the Project
To build the project, run the provided build script:

```
./build.sh
```

This script:

- Uses Maven to build the project.
- Launches Docker containers via docker-compose.
- Copies the generated WAR file to the Tomcat container and restarts it.

#### Launching Containers
Use docker-compose to start the containers:

```
docker-compose up -d
```
The docker-compose file sets up three services:
- `db`: PostgreSQL container.
- `app`: Tomcat container running the backend.
- `frontend`: Nginx container serving the static frontend.

### **Development Environment**
The project is implemented without external frameworks (e.g., no Spring), using plain Java servlets and JDBC, adhering to OOP and SOLID principles.
