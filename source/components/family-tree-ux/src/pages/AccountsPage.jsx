import React, { useEffect, useState } from "react";
import api from "../api/axiosInstance";

const AccountsPage = () => {
  const [loading, setLoading] = useState(true);
  const [accounts, setAccounts] = useState([]);
  const [selectedAccountId, setSelectedAccountId] = useState("");
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [accountDetails, setAccountDetails] = useState(null);
  const [transactions, setTransactions] = useState([]);

  const years = [2023, 2024, 2025];
  const months = [
    { name: "Jan", value: 1 }, { name: "Feb", value: 2 }, { name: "Mar", value: 3 },
    { name: "Apr", value: 4 }, { name: "May", value: 5 }, { name: "Jun", value: 6 },
    { name: "Jul", value: 7 }, { name: "Aug", value: 8 }, { name: "Sep", value: 9 },
    { name: "Oct", value: 10 }, { name: "Nov", value: 11 }, { name: "Dec", value: 12 },
  ];

  useEffect(() => {
    const fetcAccountList = async () => {
        try {
          const requestBody = {};
          const res = await api.post("/family/getAccountList", requestBody);
          setAccounts(res.data.accountList);
          setLoading(false);
        } catch (err) {
          console.error("Failed to fetch accounts", err);
          if (err.response?.data?.operationMessage) {
            // API returned an error in payload
            //setError(err.response?.data?.operationMessage);
          } else {
            //setError(ERROR_MESSAGES.DEFAULT);
          }
        }
    };
    fetcAccountList();
  }, []);

  const fetchStatement = async () => {
    try {
        // const requestBody = {};
        const res = await api.post("/family/getAccountStatement", {
            
                accountId: parseInt(selectedAccountId, 10),
                year: selectedYear,
                month: selectedMonth,
            
        });
        setAccountDetails(res.data.accountDetails);
        setTransactions(res.data.transactions);
    } catch (err) {
        console.error("Failed to fetch account statement", err);
        if (err.response?.data?.operationMessage) {
        // API returned an error in payload
        //setError(err.response?.data?.operationMessage);
        } else {
        //setError(ERROR_MESSAGES.DEFAULT);
        }
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Account Statement</h2>
      <div className="d-flex gap-2 align-items-center mb-3">
        <select
          className="form-select form-select-lg w-auto"
          value={selectedAccountId}
          onChange={(e) => setSelectedAccountId(e.target.value)}
        >
          <option value="">Select Account</option>
          {accounts.map((acc) => (
            <option key={acc.accountId} value={acc.accountId}>
              {acc.accountName}
            </option>
          ))}
        </select>
         <select
            value={selectedYear}
            onChange={(e) => setSelectedYear(e.target.value)}
            className="form-select form-select-lg w-auto"
        >
            <option value="">Select Year</option>
            {[2023, 2024, 2025].map((year) => (
            <option key={year} value={year}>
                {year}
            </option>
            ))}
        </select>

        <select
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(e.target.value)}
            className="form-select form-select-lg w-auto"
        >
            <option value="">Select Month</option>
            {[
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
            ].map((month, index) => (
            <option key={month} value={index + 1}>
                {month}
            </option>
            ))}
        </select>
        <button
            onClick={fetchStatement}
            className="btn btn-primary form-select-lg"
        >
            Go
        </button>
      </div>

      {accountDetails && (
        <div className="border p-4 rounded shadow mb-4">
          <h3 className="font-semibold text-lg mb-2">Account Details</h3>
          <p><strong>Account:</strong> {accountDetails.accountName}</p>
          <p><strong>Balance:</strong> ₹{accountDetails.currentBalance}</p>
          <p><strong>Manager:</strong> {accountDetails.accountManager}</p>
          <p><strong>Bank:</strong> {accountDetails.bankName}</p>
          <p><strong>Bank Account ID:</strong> {accountDetails.bankAccountId}</p>
          <p><strong>Type:</strong> {accountDetails.bankAccountType}</p>
        </div>
      )}

      {transactions.length > 0 && (
        <div className="overflow-x-auto">
          <table className="min-w-full table-auto border">
            <thead>
              <tr className="bg-gray-200">
                <th className="p-2 border">Date</th>
                <th className="p-2 border">Description</th>
                <th className="p-2 border">Credit</th>
                <th className="p-2 border">Debit</th>
                <th className="p-2 border">Balance</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((txn) => (
                <tr key={txn.transactionId}>
                  <td className="p-2 border">{txn.txnDate}</td>
                  <td className="p-2 border">{txn.description}</td>
                  <td className="p-2 border text-green-600">
                    {txn.txnType === "C" ? `₹${txn.amount}` : ""}
                  </td>
                  <td className="p-2 border text-red-600">
                    {txn.txnType === "D" ? `₹${txn.amount}` : ""}
                  </td>
                  <td className="p-2 border">₹{txn.balanceAfterTransaction}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default AccountsPage;
