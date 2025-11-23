import React, { useState, useEffect } from "react";
import { getPrices, addStock } from "./api";
import "./App.css";

function App() {
    const [stocks, setStocks] = useState([]);
    const [data, setData] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

    useEffect(() => {
        fetchData();
        const interval = setInterval(fetchData, 60000); // every 1 min
        return () => clearInterval(interval);
    }, []);

    async function fetchData() {
        const result = await getPrices();
        setData(result);
    }

    const filteredData = searchTerm
        ? data.filter(item =>
            item.symbol.toLowerCase().includes(searchTerm.toLowerCase())
        )
        : data;

    return (
        <div className="container">
            <h1>NSE vs BSE Price Comparison</h1>

            <StockInput setStocks={setStocks} setSearchTerm={setSearchTerm} />

            {data.length === 0 ? (
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
                    {filteredData.map(item => (
                        <tr key={item.symbol}>
                            <td>{item.symbol}</td>
                            <td>₹{item.nse}</td>
                            <td>₹{item.bse}</td>
                            <td style={{ color: item.diff >= 0 ? "green" : "red" }}>
                                ₹{item.diff}
                            </td>
                            <td>{new Date(item.time).toLocaleTimeString()}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

// ----------------------------
function StockInput({ setStocks, setSearchTerm }) {
    const [input, setInput] = useState("");

    const handleAdd = async () => {
        const stock = input.trim().toUpperCase();
        if (!stock) return;

        await addStock(stock);      // 🔥 send to backend
        setStocks(prev => (prev.includes(stock) ? prev : [...prev, stock]));

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
