import React from 'react';
import { FaWhatsapp } from 'react-icons/fa';

export const getCountryFlag = (phoneNumber) => {
  if (!phoneNumber) return '';
  if (phoneNumber.startsWith('+91')) return '🇮🇳';
  if (phoneNumber.startsWith('+1')) return '🇺🇸';
  return '🌐';
};

export const getFormattedPhoneDisplay = (phoneNumber, isWhatsapp) => {
  if (!phoneNumber) return '';
  const flag = getCountryFlag(phoneNumber);
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center' }}>
      {flag} {phoneNumber}
      {isWhatsapp && (
        <FaWhatsapp
          color="green"
          size={22}
          style={{ marginLeft: '6px' }}
          title="WhatsApp enabled"
        />
      )}
    </span>
  );
};