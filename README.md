## 🎯 Project Overview

This system addresses the growing concern of childhood obesity by providing healthcare professionals with an intelligent, distributed platform for early risk detection. The application enables multiple healthcare centers to collaborate by sharing patient data and collectively improving prediction accuracy through continuous model retraining.

---

## ✨ Key Features

- **Distributed Architecture**: Multi-client system using Java RMI for seamless communication between healthcare centers  
- **Machine Learning Integration**: Decision tree algorithms powered by Weka for transparent and interpretable predictions  
- **Automatic Model Training**: Initial model training triggered when patient database reaches 1000+ records  
- **Real-time Collaboration**: Multiple healthcare centers can access and contribute to the centralized patient database  
- **Interactive Visualization**: Decision tree visualization and comprehensive model performance reports  
- **Patient Management**: Complete CRUD operations for patient records
- **Smart Retraining**: Automatic model updates when new data exceeds 5% of existing records  

---

## 🛠️ Technologies Used

- **Backend**: Java, Java RMI  
- **Frontend**: JavaFX, Scene Builder  
- **Database**: MySQL, MySQL Workbench  
- **Machine Learning**: Weka (Decision Trees)  
- **Development Environment**: IntelliJ IDEA  
- **Visualization**: GraphViz (for decision tree rendering) 

## 📊 Data Model

The system analyzes various factors to predict obesity risk:

### 🧑‍⚕️ Patient Characteristics
- **Personal Info**: Age, Gender, Height, Weight  
- **Family History**: Overweight family background  
- **Dietary Habits**: High-calorie food consumption, vegetable intake, meal frequency  
- **Physical Activity**: Exercise frequency, screen time  
- **Lifestyle**: Smoking, alcohol consumption, transportation mode  

### 🏷️ Prediction Categories
- Insufficient Weight  
- Normal Weight  
- Overweight Level I & II  
- Obesity Type I, II & III  

---

## 📝 Conclusion

This project demonstrates the practical application of **distributed systems** and **machine learning** in healthcare, contributing to **early childhood obesity prevention and intervention**.
