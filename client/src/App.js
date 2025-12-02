// src/App.js
import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

import Signup from "./Signup";
import Login from "./Login";
import Users from "./Users";

import { addStock } from "./api";
import "./App.css";

function App() {
    const [userLoggedIn, setUserLoggedIn] = useState(false);
    const user = localStorage.getItem("token");
    useEffect(() => {
        const savedUser = localStorage.getItem("token");
        if (savedUser) {
            setUserLoggedIn(true);
        }
    }, []);
    // --- STOCK DASHBOARD STATE ---
    const [data, setData] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    // --- STOCK DASHBOARD SSE ---
    useEffect(() => {
        if (!userLoggedIn) return; // only subscribe if logged in
        const eventSource = new EventSource("/api/prices-stream");

        eventSource.onmessage = (event) => {
            const prices = JSON.parse(event.data);
            setData(prices);
        };

        eventSource.onerror = (err) => {
            console.log("SSE Error:", err);
        };

        return () => eventSource.close();
    }, [userLoggedIn]);

    const handleAddStock = async (symbol) => {
        await addStock(symbol);
    };

    // --- Filter stock data ---
    const filteredData = searchTerm
        ? data.filter(item =>
            item.body &&
            item.body[0]?.symbol &&
            item.body[0].symbol.toLowerCase().includes(searchTerm.toLowerCase())
        )
        : data;

    // --- DASHBOARD COMPONENT ---
    const Dashboard = () => (
        <div className="container">
            <h1>NSE vs BSE Price Comparison</h1>
            <StockInput setSearchTerm={setSearchTerm} handleAddStock={handleAddStock} />

            {filteredData.length === 0 ? (
                <p>No stocks added yet. Add a stock above.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>Stock</th>
                        <th>NSE</th>
                        <th>BSE</th>
                        <th>Difference</th>
                        <th>Updated</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredData.map((item, idx) => {
                        const s = item.body?.[0] || {};
                        return (
                            <tr key={idx}>
                                <td>{s.longName ?? "-"}</td>
                                <td>₹{s.nsePrice ?? "-"}</td>
                                <td>₹{s.bsePrice ?? "-"}</td>
                                <td style={{ color: s.priceDiff >= 0 ? "green" : "red" }}>
                                    ₹{s.priceDiff != null ? s.priceDiff.toFixed(3) : "-"}
                                </td>
                                <td>{s.time ? new Date(s.time).toLocaleTimeString() : "-"}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );

    return (

        <Router>
            <Routes>
                {/* Signup as default */}
                <Route path="/signup" element={<Signup onSignupSuccess={() => window.location.href="/login"} />} />
                <Route path="/login" element={<Login onLogin={() => setUserLoggedIn(true)} />} />
                <Route path="/dashboard" element={userLoggedIn ? <Dashboard /> : <Navigate to="/signup" />} />
                <Route path="/users" element={user ? <Users /> : <Navigate to="/login" />} />
                <Route path="/stocks" element={user ? <Dashboard /> : <Navigate to="/login" />} />

                {/* Redirect all unknown paths to signup */}
                <Route path="*" element={<Navigate to="/signup" replace />} />
            </Routes>
        </Router>

    );
}

function StockInput({ setSearchTerm, handleAddStock }) {
    const [input, setInput] = useState("");

    const handleAdd = async () => {
        const stock = input.trim().toUpperCase();
        if (!stock) return;
        await handleAddStock(stock);
        setInput("");
    };

    const handleSearch = () => setSearchTerm(input.trim());

    return (
        <div className="add-stock">
            <input
                type="text"
                placeholder="Enter stock symbol"
                value={input}
                onChange={e => setInput(e.target.value)}
            />
            <button onClick={handleAdd}>Add Stock</button>
            <button onClick={handleSearch}>Search</button>
        </div>
    );
}

export default App;
