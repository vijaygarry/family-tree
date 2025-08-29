import React from "react";
import { useAuth } from "../context/AuthContext";
import StatsCounterSection from "../components/StatsCounterSection";

const HomePage = () => {
  const { user } = useAuth();

  return (
    <div className="container p-4 bg-white rounded mt-4">
      {/* <h2>Welcome, {user?.firstName || "Family Member"}!</h2> */}

      <div className="row">
        <div className="col-sm-6">
          <div className="about-img">
          <img src="/about-img.jpeg" alt=""  className="w-100" />
          </div>
        </div>
        <div className="col-sm-6">
          <div className="about-content">
            <h3 className="mb-4 fw-bold">About Chippa Samaj</h3>
            {/* <h4 className="mb-4 fs-2 fw-light">Quisque pulvinar orci purus, quis pharetra tortor sodales nec</h4> */}
            <p>Chhipa is a caste of people with ancestral roots tracing back to India. These people are basically Rajputs and used to wear Kshatriya attire. These people were skilled in the art of war, Later people of this caste started doing printing work. They are found in the states of Uttar Pradesh, Maharastra, Gujarat, Rajasthan, Madhya Pradesh, Haryana, Delhi. <br /><br />

            According to historians, the Chhipa were originally a warrior class or Kshatriya Rajput. They used to have a similar lifestyle like a Rajput in which Physical activities such as hunting, and warfare were involved. It is said that once, according to the Hindu epic Mahabharata, Lord Parshuram While killing all the Kshatriyas to avenge their father, two brothers from the Rajput clan took refuge in a temple. One of the presiding deities hid behind the statue. And it got its name from the literal 'hide' for the Hindi verb 'Chhipa'. </p>
          </div>
        </div>
      </div>

      <div className="bg-body-secondary p-4 rounded">
        <div>
          <StatsCounterSection />
        </div>
      </div>

      <div className="my-5">
        <div className="row">
          <div className="col-sm-4">
            <img src="/brand1.jpg" alt="" className="w-100" />
          </div>
          <div className="col-sm-4">
            <img src="/brand2.jpg" alt="" className="w-100" />
          </div>
          <div className="col-sm-4">
            <img src="/brand3.jpg" alt="" className="w-100" />
          </div>
        </div>
      </div>
      
    </div>
  );
};

export default HomePage;
