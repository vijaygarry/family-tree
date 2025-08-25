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
          <img src="/about-img.jpg" alt=""  className="w-100" />
          </div>
        </div>
        <div className="col-sm-6">
          <div className="about-content">
            <h3 className="mb-4 fw-bold">About Chippa Samaj</h3>
            <h4 className="mb-4 fs-2 fw-light">Quisque pulvinar orci purus, quis pharetra tortor sodales nec</h4>
            <p>Lorem ipum dolor sit amet, consectetur adipiscing elit. Donec in magna quam. Nulla pharetra tincidunt mi non mollis. Nam sem libero, pulvinar eget efficitur non, blandit et metus. Quisque id justo arcu. <br /><br />

Phasellus elit lacus, convallis eu ante id, placerat dapibus augue. Curabitur sodales nisi quis ligula pulvinar, vel vestibulum leo convallis. Nulla nisl enim, mattis sed ultricies vitae, semper eget dolor. </p>
          </div>
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

      <div className="bg-body-secondary p-4 rounded">
        <div>
          <StatsCounterSection />
    </div>
      </div>

    </div>
  );
};

export default HomePage;
