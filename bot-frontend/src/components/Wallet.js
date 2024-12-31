import '../css/app.css'; 
import React from 'react';

const Wallet = ({ totalBalance }) => {
  return (
    <div className="card shadow-sm">
      <div className="card-body text-center">
        <h5 className="card-title">cüzdan bakiyesi</h5>
        <h2 className="card-text text-success">
         
          {totalBalance?.totalEq || 'Yükleniyor...'}
        </h2>
      </div>
    </div>
  );
};

export default Wallet;
