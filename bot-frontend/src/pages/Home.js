import React from 'react'; 
import WalletPage from './WalletPage';
import CryptoInfo from '../components/CryptoInfo';
import '../css/Home.css'; 
const Home = () => {
  return (
    <div className="home-container">
      <div className="crypto-info-container">
        <CryptoInfo />
      </div>
      <div className="wallet-page-container">
        <WalletPage />
      </div>
    </div>
  );
};

export default Home;
