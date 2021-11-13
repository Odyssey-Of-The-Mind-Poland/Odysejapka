# Odysejapka

Server app for Odyssey of the mind in Poland contest

## API Docs

Default url: ``grzybek.snet.ovh:8081``

### Time table

```http
GET /timeTable
```

Returns list of all performances

```http
POST /timeTable
```

Add new performance

| param | Type | Description |
| :--- | :--- | :--- |
| `id` | `int` | Id of performance |
| `city` | `String` | Name of the city |
| `team` | `String` | Name of the team |
| `problem` | `int` | Id of problem |
| `age` | `int` | Id of age group |
| `stage` | `int` | Id of stage |
| `performance` | `String` | Hour of performance |
| `spontan` | `String` | Hour of spontan |

```http
PUT /timeTable
```

Updates existing performance

| param | Type | Description |
| :--- | :--- | :--- |
| `id` | `int` | Id of performance |
| `city` | `String` | Name of the city |
| `team` | `String` | Name of the team |
| `problem` | `int` | Id of problem |
| `age` | `int` | Id of age group |
| `stage` | `int` | Id of stage |
| `performance` | `String` | Hour of performance |
| `spontan` | `String` | Hour of spontan |

```http
DELETE /timeTable/{id}
```

Delete existing performance

### User


