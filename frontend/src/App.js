import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Layout } from 'antd';
import TaskList from './components/TaskList';
import TaskDetail from './components/TaskDetail';
import TaskForm from './components/TaskForm';
import Statistics from './components/Statistics';
import Header from './components/Header';
import './App.css';

const { Content } = Layout;

function App() {
  return (
    <div className="app-container">
      <Layout style={{ minHeight: '100vh', background: 'transparent' }}>
        <Header />
        <Content style={{ padding: '24px', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
          <Routes>
            <Route path="/" element={<TaskList />} />
            <Route path="/task/:id" element={<TaskDetail />} />
            <Route path="/task/new" element={<TaskForm />} />
            <Route path="/task/edit/:id" element={<TaskForm />} />
            <Route path="/statistics" element={<Statistics />} />
          </Routes>
        </Content>
      </Layout>
    </div>
  );
}

export default App;