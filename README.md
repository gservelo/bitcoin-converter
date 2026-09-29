# Conversor BTC 🪙

> **Autora:** Giovana Machado Servelo — RA 172317630  
> **Disciplina:** Usabilidade em Dev Web, Mobile e Jogos  
> **Exercício:** Conversão de Bitcoin (BRL ↔ BTC)


Aplicativo Android para conversão entre **Real Brasileiro (BRL)** e **Bitcoin (BTC)**, desenvolvido em Kotlin com Android Studio.

## Funcionalidades

- 🔄 **Conversão bidirecional** — R$ → BTC e BTC → R$
- 🌐 **Cotação automática** — busca o preço em tempo real via API CoinGecko (gratuita, sem chave)
- ✏️ **Cotação manual** — usuário pode inserir a cotação manualmente (útil offline)
- 📋 **Histórico** — mantém as últimas 5 conversões realizadas
- 🎨 **Visual Bitcoin** — tema com a cor laranja oficial do Bitcoin (#F7931A)

## Capturas de Tela

> _Adicione screenshots do emulador ou dispositivo real aqui._

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Android SDK 34 (min 24) | Plataforma |
| Material Components 3 | UI/UX |
| Retrofit 2 + OkHttp | Chamadas de rede |
| Gson | Parse de JSON |
| CoinGecko API | Cotação em tempo real |
| ViewBinding | Acesso seguro às views |
| Coroutines | Requisições assíncronas |

## Estrutura do Projeto

```
BitcoinConverter/
├── app/
│   └── src/main/
│       ├── java/com/example/bitcoinconverter/
│       │   ├── MainActivity.kt      # Tela principal e lógica de conversão
│       │   ├── Models.kt            # Data classes (resposta API + histórico)
│       │   ├── CoinGeckoApi.kt      # Interface Retrofit
│       │   └── RetrofitClient.kt    # Singleton do cliente HTTP
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/ (strings, colors, themes)
└── build.gradle
```

## Como executar

1. Clone o repositório:
   ```bash
   git clone <URL_DO_REPOSITÓRIO>
   ```
2. Abra a pasta `BitcoinConverter` no **Android Studio** (Electric Eel ou superior)
3. Aguarde a sincronização do Gradle
4. Execute no emulador ou dispositivo com **Android 7.0+** (API 24)

> ⚠️ O app precisa de **conexão com a internet** para buscar a cotação automaticamente. Sem internet, use o modo de cotação manual.

## API utilizada

[CoinGecko](https://www.coingecko.com/en/api) — endpoint `simple/price`  
Gratuita, sem necessidade de chave de API.

```
GET https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=brl
```

## Exercício

Projeto desenvolvido para a disciplina **Usabilidade em Dev Web, Mobile e Jogos**.

