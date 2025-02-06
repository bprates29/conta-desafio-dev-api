# Conta Digital API

## Descrição
Esta API permite a gestão de contas digitais, incluindo funcionalidades como:
- Criação e remoção de holders/clientes
- Consulta de contas
- Realização de saques e depósitos
- Consulta de extrato por período
- Bloqueio e desbloqueio de contas
- Fechamento de conta

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **Spring Web**
- **Spring Boot Validation**
- **MySQL**
- **Docker & Docker Compose**
- **JUnit 5 & Mockito**

## Estrutura do Projeto

```
conta-desafio-dev-api/
├── src/
│   ├── main/
│   │   ├── java/br/com/bprates/conta_desafio_dev_api/
│   │   │   ├── configs/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── domain/
│   │   │   ├── dtos/
│   │   │   ├── utils/
│   │   ├── resources/
│   │   │   ├── application.properties
│   ├── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
```

## Como Executar a API
### Usando Docker

1. **Construa e suba os containers**:
```bash
docker-compose up --build -d
```

2. **Verifique se os containers estão rodando**:
```bash
docker ps
```

3. **A API estará acessível em**:
```
http://localhost:8080/api
```

### Sem Docker (Usando Maven)

1. **Configurar o banco de dados MySQL** (caso não esteja rodando via Docker).
2. **Rodar a aplicação**:
```bash
mvn spring-boot:run
```
3. **Acessar a API**:
```
http://localhost:8080/api
```

## Endpoints Disponíveis
### **Criar um holder/cliente**
```http
POST /api/accounts
```
#### **Request Body**
```json
{
  "cpf": "12345678900"
}
```
#### 🔹 **Response**
```json
{
  "agencyNumber": "0001",
  "accountNumber": "100123",
  "balance": 0.00
}
```
---
### **Listar todas as contas**
```http
GET /api/accounts
```
#### **Response**
```json
[
  {
    "agencyNumber": "0001",
    "accountNumber": "100123",
    "balance": 250.00
  },
  {
    "agencyNumber": "0001",
    "accountNumber": "100124",
    "balance": 1500.00
  }
]
```
---
### **Realizar um Depósito**
```http
POST /api/accounts/agency/{agency}/account/{account}/deposit
```
#### **Request Body**
```json
{
  "amount": 500.00
}
```
#### **Response**
```json
{
  "agencyNumber": "0001",
  "accountNumber": "100123",
  "balance": 750.00
}
```
---
### **Consultar Extrato por Período**
```http
GET /api/accounts/agency/{agency}/account/{account}/statement?startDate=2025-02-01&endDate=2025-02-06
```
#### **Response**
```json
[
  {
    "type": "DEPOSIT",
    "amount": 500.00,
    "timestamp": "2025-02-06T12:00:00"
  },
  {
    "type": "WITHDRAW",
    "amount": 100.00,
    "timestamp": "2025-02-06T14:30:00"
  }
]
```
---
### **Fechar uma Conta**
```http
DELETE /api/accounts/agency/{agency}/account/{account}
```
#### **Response**
```http
204 No Content
```
---

## Testes
Rodar testes unitários:
```bash
mvn test
```

## Melhorias Futuras
- Implementar autenticação e autorização com JWT
- Adicionar logs estruturados
- Criar testes de integração
- Implementar monitoramento com Prometheus/Grafana

## Licença
Este projeto é distribuído sob a MIT License.

## Autor
**Bernardo Prates** - [LinkedIn](https://www.linkedin.com/in/bprates/)

