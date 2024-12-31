import React from 'react';
import '../css/app.css'; 
const CryptoList = ({ cryptos }) => {
  return (
    <div className="card shadow-sm">
      <div className="card-body">
        <h5 className="card-title">Cryptocurrencies</h5>
        <ul className="list-group">
          {cryptos.length > 0 ? (
            cryptos.map((crypto, index) => (
              <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                <span>{crypto.ccy}:</span>
                <span className="spot-bal text-center text-success">{crypto.spotBal}</span>
              </li>
            ))
          ) : (
            <li className="list-group-item text-center">No cryptocurrencies available</li>
          )}
        </ul>
      </div>
    </div>
  );
};

export default CryptoList;
