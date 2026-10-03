# 🏢 Projeto de Gerenciamento de Condomínios — Java

API para gerenciamento de condomínios, usuários, imóveis e moradores, com suporte a múltiplos condomínios. A aplicação Java integra-se ao SQL Server e ao RabbitMQ e oferece autenticação baseada em JWT.

> **Status: em desenvolvimento.** Este projeto foi gerado no Visual Studio com o GitHub Copilot Chat, a partir do código-fonte de uma API ASP.NET Core. Consulte a observação sobre o processo de conversão ao final deste README.

## 🏗️ Arquitetura e tecnologias

- **Java 21**
- **Spring Boot 3.2.5** — API REST
- **Spring Data JPA / Hibernate** — persistência
- **Spring Security e JWT** — autenticação e autorização
- **Spring AMQP / RabbitMQ** — mensageria e processamento de e-mails
- **SQL Server 2022** — banco de dados
- **Springdoc OpenAPI / Swagger UI** — documentação da API
- **Maven** — dependências e build
- **Docker e Docker Compose** — execução em containers

O repositório contém a API Java. Os frontends React e Angular são projetos separados e não são iniciados pelo `docker-compose.yml` deste repositório.

## 🔧 Pré-requisitos

- Docker Desktop com Docker Compose
- Java JDK 21 e Maven, caso queira compilar ou executar a aplicação fora do Docker
- SQL Server e RabbitMQ acessíveis pela aplicação
- Variáveis de ambiente configuradas para o ambiente local

## 🚀 Execução com Docker

### 1. Configure as variáveis de ambiente

O `docker-compose.yml` carrega um arquivo `.env` na raiz e usa variáveis desse arquivo para configurar a aplicação e a rede Docker. Crie-o localmente e configure, no mínimo:

- `DOCKER_NETWORK`, `HOST_PORT` e `CONTAINER_PORT`
- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_USERNAME` e `SPRING_RABBITMQ_PASSWORD`
- `APP_JWT_KEY`, `APP_JWT_ISSUER`, `APP_JWT_AUDIENCE` e `APP_JWT_EXPIREMINUTES`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`, `LOGGING_LEVEL_ROOT` e `MANAGEMENT_ENDPOINT_HEALTH_SHOWDETAILS`

Use valores próprios para cada ambiente e não publique arquivos com senhas, chaves JWT ou outras credenciais.

### 2. Prepare SQL Server e RabbitMQ

Este Compose não cria os containers do SQL Server nem do RabbitMQ; ele conecta a API a uma rede Docker externa. Se for usar o script de infraestrutura incluído, configure localmente o arquivo `.env.prod` exigido por ele e execute no PowerShell:

```powershell
.\Infraestrutura-Base.ps1
```

O script prepara a rede e os containers da infraestrutura. Verifique se os nomes de rede, hosts e credenciais configurados para a API correspondem aos serviços criados. Em outros ambientes, disponibilize serviços equivalentes e configure as variáveis de conexão.

O SQL Server deve estar acessível e conter o schema esperado pela aplicação. No perfil de produção, o Hibernate está configurado para validar o schema, não para atualizá-lo automaticamente.

### 3. Inicie a API

Na raiz do repositório, execute:

```powershell
docker compose up --build -d
```

Para acompanhar os logs:

```powershell
docker compose logs -f CondominioSaaS-java
```

Para parar o container da API:

```powershell
docker compose down
```

### Execução local com Maven

Com Java 21, Maven, SQL Server e RabbitMQ configurados, compile e execute com:

```powershell
mvn clean package
java -jar target/Docker_RabbitMQ_Java_Condominio-0.0.1-SNAPSHOT.jar
```

O perfil `dev` possui configurações locais em `src/main/resources/application-dev.yml`. Ajuste-as ao seu ambiente antes de executar; não reutilize credenciais de desenvolvimento em produção.

## 🔗 Portas e endereços

| Serviço | Endereço padrão | Observação |
|---|---|---|
| API Java | `http://localhost:8081` | A porta pode ser alterada por `HOST_PORT` e `CONTAINER_PORT` |
| Swagger UI | `http://localhost:8081/swagger-ui/index.html` | A documentação é disponibilizada pela API |
| RabbitMQ Management | `http://localhost:15672` | Requer RabbitMQ com o plugin de gerenciamento |
| SQL Server | `localhost:1433` | Porta padrão publicada pelo script de infraestrutura |

As portas e credenciais dos serviços externos dependem da configuração local.

## 🔐 Autenticação

A API usa JWT. No carregamento inicial, a aplicação cria um usuário de suporte caso ele ainda não exista. Os valores padrão definidos no código são usuário `Admin` e senha `12345`, com solicitação de troca de senha no primeiro acesso. Altere essas credenciais e confirme o fluxo de primeiro acesso antes de disponibilizar o sistema em qualquer ambiente compartilhado ou de produção.

Os perfis previstos no código incluem **Suporte**, **Síndico** e **Porteiro**. Como o projeto está em desenvolvimento, valide as permissões e os fluxos de autenticação antes de integrar ou publicar clientes.

## 📧 Mensageria

A aplicação integra-se ao RabbitMQ para o processamento assíncrono de e-mails. Os nomes padrão declarados no código incluem a fila `fila_emails`, a exchange `email_exchange_`, a dead-letter exchange `dlx_exchange` e a dead-letter queue `fila_emails_erro`. Confirme os nomes e bindings efetivos no broker e alinhe-os à configuração usada no ambiente.

## 🗄️ Banco de dados

A aplicação utiliza SQL Server via JDBC e JPA/Hibernate. A configuração padrão aponta para o banco `Condominio_aspnet`; a URL, o usuário e a senha devem ser fornecidos pelas variáveis de ambiente. A inicialização pode criar o banco quando habilitada, mas isso não substitui a validação do schema e das regras de negócio.

Para consultar as tabelas, veja o arquivo [`Consultas SQL das Tabelas.sql`](Consultas%20SQL%20das%20Tabelas.sql). Confira o nome do banco e o schema disponíveis na sua instalação antes de executar as consultas.

## 🔗 Projetos relacionados

- [API original em ASP.NET Core](https://github.com/WaineAlvesCarneiro/Docker_RabbitMQ_AspNetCore_Condominio)
- [Frontend React](https://github.com/WaineAlvesCarneiro/Docker_RabbitMQ_React_Condominio)
- [Frontend Angular](https://github.com/WaineAlvesCarneiro/Docker_RabbitMQ_Angular_Condominio)

Os frontends são independentes e podem precisar de ajustes para compatibilidade com os contratos e comportamentos atuais desta API Java.

## 👨‍💻 Desenvolvedor

Criado por **Waine Alves Carneiro**.

[Perfil no GitHub](https://github.com/WaineAlvesCarneiro?tab=repositories)

## 📝 Observação sobre o processo de conversão

Este projeto foi gerado no Visual Studio com o GitHub Copilot Chat. A solicitação foi criar, com base no código-fonte da API ASP.NET Core aberto no Visual Studio, uma versão em Java com as mesmas funcionalidades e regras de negócio.

Durante a conversão, foram necessários diversos ajustes no código gerado. Entre os pontos identificados:

- **Entidades e tipos:** algumas entidades Java foram geradas com tipos diferentes dos utilizados na versão ASP.NET Core.
- **Integração com os frontends:** essas diferenças fizeram com que as aplicações Angular e React não conseguissem realizar algumas requisições inicialmente, exigindo ajustes.
- **Regras de negócio:** nem todas as regras foram reproduzidas como no sistema original. Algumas regras foram geradas de forma diferente, por interpretação incorreta ou por decisões próprias do Copilot.
- **Troca de senha no primeiro acesso:** esse fluxo é um exemplo de comportamento que precisa ser comparado e validado em relação à API original.

Por isso, o projeto continua em desenvolvimento. As funcionalidades, integrações e regras de negócio devem ser revisadas antes de considerar esta versão equivalente à API ASP.NET Core.
