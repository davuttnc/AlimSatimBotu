import React, { useEffect, useState } from 'react';
import { fetchBalance } from '../services/api';
import Wallet from '../components/Wallet';
import CryptoList from '../components/CryptoList';
import '../css/app.css'; 
const WalletPage = () => {
  const [totalBalance, setTotalBalance] = useState(null);
  const [cryptos, setCryptos] = useState([]);

  useEffect(() => {
    const loadBalance = async () => {
      try {
        const [total, cryptoList] = await fetchBalance();
        setTotalBalance(total);
        setCryptos(cryptoList);
      } catch (error) {
        console.error("Failed to load balance data:", error);
      }
    };
    loadBalance();
  }, []);

  return (
    <div className="container mt-5">
      {/* Wallet Section */}
      <div className="row">
        <div className="col-12">
          <Wallet totalBalance={totalBalance} />
        </div>
      </div>

      {/* Crypto List Section */}
      <div className="row mt-4">
        <div className="col-12">
          <CryptoList cryptos={cryptos} />
        </div>
      </div>
    </div>
  );
};

export default WalletPage;
