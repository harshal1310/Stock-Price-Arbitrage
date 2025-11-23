import React, { useState, useEffect } from "react";
import { addStock } from "./api";
import "./App.css";

function App() {
    const [data, setData] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    useEffect(() => {
        const eventSource = new EventSource("/api/prices-stream");

        eventSource.onmessage = (event) => {
            const prices = JSON.parse(event.data);
            console.log("Live Data:", prices);
            setData(prices);
        };

        eventSource.onerror = (err) => {
            console.log("SSE Error:", err);
        };

        return () => eventSource.close();
    }, []);

    const filteredData = searchTerm
        ? data.filter(item =>
            item.body &&
            item.body[0]?.symbol &&
            item.body[0].symbol.toLowerCase().includes(searchTerm.toLowerCase())
        )
        : data;

    return (
        <div className="container">
            <h1>NSE vs BSE Price Comparison</h1>

            <StockInput setSearchTerm={setSearchTerm} />

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
}

function StockInput({ setSearchTerm }) {
    const [input, setInput] = useState("");

    const handleAdd = async () => {
        const stock = input.trim().toUpperCase();
        if (!stock) return;

        await addStock(stock);
        setInput("");
    };

    const handleSearch = () => {
        setSearchTerm(input.trim());
    };

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
