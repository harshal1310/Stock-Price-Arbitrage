#  Real-Time Stock Price Arbitrage Detection Platform

A full-stack application that detects **real-time arbitrage opportunities** between NSE and BSE using live stock market data.  
Built with **Spring Boot**, **React**, and the **Yahoo Finance API**, this platform streams stock updates live using **Server-Sent Events (SSE)** and highlights profitable price mismatches.

---

##  Features

-  **Real-time stock price updates** (polling + SSE streaming)
-  **Side-by-side NSE vs BSE comparison**
-  **Arbitrage opportunity detection logic**
-  **Async fetch operations with scheduler + caching**
-  **Full-stack: Spring Boot backend + React frontend**
-  **PostgreSQL storage for stock metadata**
-  **Yahoo Finance API Integration**

---


## Quick Start (macOS)

1. Clone
```bash
git clone <repo-url>
cd arbitrage
```

2. Environment
   Create a `.env` (project root) with:
```env
RAPIDAPI_KEY=your_rapidapi_key
```

3. Start backend & DB (provided helper)
```bash
chmod +x start.sh
./start.sh
```
Or run backend directly:
```bash
./gradlew bootRun
```

4. Start frontend
```bash
cd client
npm install
npm start
```

