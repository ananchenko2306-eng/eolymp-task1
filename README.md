# Currency Converter REST API

Це простий REST API для конвертації валют за актуальним курсом "ПриватБанку".  
Додаток побудований на Java (Servlets) та використовує бібліотеку Jackson для обробки даних у форматі JSON.

**Базовий URL:** `https://currency-converter-8rad.onrender.com`

---

## 1. Конвертація валют

Цей запит дозволяє конвертувати одну валюту в іншу.
Курс береться з API ПриватБанку та конвертація відбувається через гривню.

* **URL:** `/api/exchange`
* **Метод:** `GET`
* **Обов'язкові параметри запиту:**
  * `from` — валюта, яку ви віддаєте (наприклад, `USD`, `EUR`, `UAH`).
  * `to` — валюта, яку ви хочете отримати (наприклад, `PLN`, `UAH`).
  * `amount` — сума для обміну (має бути додатним числом).

Якщо валюта не підтримується — повернеться помилка.

### Приклад успішного запиту:
`GET /api/exchange?from=USD&to=UAH&amount=100`

**Відповідь (JSON):**
```json
{
  "status": "success",
  "from": "USD",
  "to": "UAH",
  "amount": 100.0,
  "result": 4150.0
}
```

### Приклад помилки:
`GET /api/exchange?from=USD&to=AUD&amount=100`

**Відповідь (JSON):**
```json
{
  "status": "error",
  "message": "Невідома валюта: AUD"
}
```

## 2. Історія операцій

Дозволяє переглянути журнал усіх запитів (включаючи помилкові).

* **URL:** `/api/history`
* **Метод:** `GET`

### Приклад відповіді:
`GET /api/history`

**Відповідь (JSON):**
```json
[
  {
    "time": "2026-03-27 10:15:42",
    "fromCurrency": "USD",
    "toCurrency": "UAH",
    "amountGiven": 100.0,
    "amountReceived": 4150.0,
    "error": "Успіх"
  },
  {
    "time": "2026-04-22 20:20:10",
    "fromCurrency": "USD",
    "toCurrency": "AUD",
    "amountGiven": 0.0,
    "amountReceived": 0.0,
    "error": "Помилка (вхідні дані: '284'): Невідома валюта: AUD"
  }
]
```
