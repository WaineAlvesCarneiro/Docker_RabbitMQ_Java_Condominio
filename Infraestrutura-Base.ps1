# Fase‑1: Infraestrutura base
$ErrorActionPreference = "Stop"

# Carregar variáveis do .env.prod
Get-Content .env.prod | ForEach-Object {
    if ($_ -match "^(.*?)=(.*)$") {
        $name  = $matches[1].Trim()
        $value = $matches[2].Trim()
        [System.Environment]::SetEnvironmentVariable($name, $value)
    }
}

Write-Host "🚀 Iniciando o provisionamento da Infraestrutura Base..." -ForegroundColor Cyan

# 1. CRIAR REDE DOCKER
$networkExists = docker network ls --format '{{.Name}}' | Where-Object { $_ -eq $env:DOCKER_NETWORK }
if (-not $networkExists) {
    Write-Host "🌐 Criando a rede Docker '$env:DOCKER_NETWORK'..." -ForegroundColor Yellow
    docker network create $env:DOCKER_NETWORK | Out-Null
    Write-Host "✅ Rede Docker '$env:DOCKER_NETWORK' criada." -ForegroundColor Green
} else {
    Write-Host "✅ Rede Docker '$env:DOCKER_NETWORK' já existe." -ForegroundColor Gray
}

# 2. SUBIR SQL SERVER 2022
$sqlExists = docker ps -a --format '{{.Names}}' | Where-Object { $_ -eq $env:SQL_CONTAINER }
if (-not $sqlExists) {
    Write-Host "🐘 Subindo container do SQL Server 2022..." -ForegroundColor Yellow
    docker run -d `
        --name $env:SQL_CONTAINER `
        --network $env:DOCKER_NETWORK `
        --restart unless-stopped `
        -e 'ACCEPT_EULA=Y' `
        -e "MSSQL_SA_PASSWORD=$env:SQL_SA_PASSWORD" `
        --mount source=$env:SQL_VOLUME,target=/var/opt/mssql `
        -p 1433:1433 `
        mcr.microsoft.com/mssql/server:2022-latest | Out-Null
} else {
    Write-Host "✅ Container '$env:SQL_CONTAINER' já existe. Garantindo que está rodando..." -ForegroundColor Gray
    docker start $env:SQL_CONTAINER | Out-Null
}

# 3. SUBIR RABBITMQ
$rabbitExists = docker ps -a --format '{{.Names}}' | Where-Object { $_ -eq $env:RABBIT_CONTAINER }
if (-not $rabbitExists) {
    Write-Host "🐇 Subindo container do RabbitMQ..." -ForegroundColor Yellow
    docker run -d `
      --name $env:RABBIT_CONTAINER `
      --network $env:DOCKER_NETWORK `
      --restart unless-stopped `
      --mount source=$env:RABBIT_VOLUME,target=/var/lib/rabbitmq `
      -p 5672:5672 `
      -p 15672:15672 `
      rabbitmq:3-management | Out-Null

    Write-Host "⏳ Aguardando RabbitMQ inicializar..." -ForegroundColor Yellow
    Start-Sleep -Seconds 25   # tempo maior para garantir que o serviço esteja pronto

    Write-Host "🔑 Criando usuário admin_geral..." -ForegroundColor Yellow
    docker exec $env:RABBIT_CONTAINER rabbitmqctl add_user $env:SPRING_RABBITMQ_USERNAME $env:SPRING_RABBITMQ_PASSWORD
    docker exec $env:RABBIT_CONTAINER rabbitmqctl set_user_tags $env:SPRING_RABBITMQ_USERNAME administrator
    docker exec $env:RABBIT_CONTAINER rabbitmqctl set_permissions -p / $env:SPRING_RABBITMQ_USERNAME ".*" ".*" ".*"

    Write-Host "✅ Usuário '$env:SPRING_RABBITMQ_USERNAME' criado." -ForegroundColor Green
    Write-Host "✅ Usuário padrão 'guest' também está disponível." -ForegroundColor Green
} else {
    Write-Host "✅ Container '$env:RABBIT_CONTAINER' já existe. Garantindo que está rodando..." -ForegroundColor Gray
    docker start $env:RABBIT_CONTAINER | Out-Null
}

Write-Host "`n✅ Concluído com sucesso! Infraestrutura base pronta." -ForegroundColor Green
