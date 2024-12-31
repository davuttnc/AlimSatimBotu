import React, { useState, useEffect } from "react";
import axios from "axios";
import { LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from "recharts";
import moment from "moment";

const CryptoInfo = () => {
  const [cryptoSymbol, setCryptoSymbol] = useState("");
  const [cryptoData, setCryptoData] = useState(null);
  const [priceHistory, setPriceHistory] = useState([]);
  const [minPrice, setMinPrice] = useState(null); // En düşük fiyat
  const [maxPrice, setMaxPrice] = useState(null); // En yüksek fiyat
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSearch = async () => {
    if (!cryptoSymbol) return;

    const symbolWithUSD = cryptoSymbol.toUpperCase() + "-USD";
    setLoading(true);
    setError(null); // Reset error on new search

    try {
      // Get real-time crypto data
      const response = await axios.get(`http://localhost:8080/api/v1/crypto/info/${symbolWithUSD}`);
      console.log(response.data);  // Log to see the response structure

      // API data for crypto
      setCryptoData(response.data);

      // Calculate timestamp intervals and map price history
      const priceHistoryData = response.data.priceHistory.map((price, index) => {
        if (price !== undefined && price !== null) {
          const timeDifferenceInMinutes = (response.data.priceHistory.length - index) * 60;
          const timestamp = moment().subtract(timeDifferenceInMinutes, 'minutes').format("HH:mm"); // Format timestamp
          return {
            timestamp,
            price: parseFloat(price).toFixed(2), // Ensure toFixed is called only on valid price
          };
        } else {
          return null; // If price is undefined, return null (or a default value like 0)
        }
      }).filter(item => item !== null); // Remove null entries
      console.log('Price History:', priceHistoryData);

      // Calculate min and max prices from the filtered price history (within 24 hours)
      const lowestPrice = Math.min(...priceHistoryData.map(item => parseFloat(item.price)));
      const highestPrice = Math.max(...priceHistoryData.map(item => parseFloat(item.price)));

      setMinPrice(lowestPrice.toFixed(2)); // Set the lowest price
      setMaxPrice(highestPrice.toFixed(2)); // Set the highest price

      // Set the price history data
      setPriceHistory(priceHistoryData);

    } catch (error) {
      console.error("Error fetching crypto data:", error);
      setError("Veri alınırken bir hata oluştu.");
      setCryptoData(null);
      setPriceHistory([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <h1 className="text-center mb-4">Alım Satım Tahmin Botu</h1>

      {/* Arama Card */}
      <div className="card">
        <div className="card-body">
          <h5 className="card-title text-center">Kripto Bilgisi Arama</h5>
          <div className="d-flex">
            <div className="form-group w-100 mb-0">
              <label htmlFor="cryptoSymbol">Kripto Para Sembolü (Örneğin: BTC)</label>
              <div className="input-group">
                <input
                  type="text"
                  className="form-control rounded-left"
                  id="cryptoSymbol"
                  placeholder="BTC, ETH gibi semboller girin"
                  value={cryptoSymbol}
                  onChange={(e) => setCryptoSymbol(e.target.value)}
                  maxLength={4}
                  style={{ marginRight: "15px", borderRadius: "50px", height: "50px" }}
                />
                <div className="input-group-append">
                  <button
                    className="btn btn-primary rounded-right"
                    onClick={handleSearch}
                    disabled={loading}
                    style={{ height: "50px", borderRadius: "40px" }}
                  >
                    {loading ? "Yükleniyor..." : "Ara"}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Error Message */}
      {error && <div className="alert alert-danger text-center">{error}</div>}

      {/* Veri ve Grafik Görüntüleme */}
      {cryptoData && (
        <div className="row mt-4">
          {/* Tablo Bölümü */}
          <div className="col-md-6">
            <div className="card">
              <div className="card-body">
                <h3 className="text-center text-danger mb-4">{cryptoData.signal}</h3>
                <table className="table table-striped">
                  <thead>
                    <tr>
                      <th scope="col">Özellik</th>
                      <th scope="col">Değer</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>Sembol</td>
                      <td>{cryptoData.symbol}</td>
                    </tr>
                    <tr>
                      <td>Fiyat</td>
                      <td>{cryptoData.price}</td>
                    </tr>
                    <tr>
                      <td>24 Saat Yüksek</td>
                      <td>{cryptoData.high24h}</td>
                    </tr>
                    <tr>
                      <td>24 Saat Düşük</td>
                      <td>{cryptoData.low24h}</td>
                    </tr>
                    <tr>
                      <td>EMA</td>
                      <td>{cryptoData.ema}</td>
                    </tr>
                    <tr>
                      <td>RSI</td>
                      <td>{cryptoData.rsi}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          {/* Grafik Bölümü */}
          <div className="col-md-6">
            <div className="card">
              <div className="card-body">
                <h5 className="card-title text-center">Fiyat Geçmişi </h5>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={priceHistory}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="timestamp" />
                    <YAxis domain={[minPrice ? parseFloat(minPrice) - 100 : 0, maxPrice ? parseFloat(maxPrice) + 100 : 10000]} />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="price" stroke="#8884d8" />
                    {/* Min and Max Price Labels */}
                    <text x="90%" y="10%" textAnchor="middle" fontSize={14} fill="#000">
                       Düşük Fiyat: {minPrice} USD
                    </text>
                    <text x="90%" y="20%" textAnchor="middle" fontSize={14} fill="#000">
                     En Yüksek Fiyat: {maxPrice} USD
                    </text>
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}; 

export default CryptoInfo;
