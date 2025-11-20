import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Row, 
  Col, 
  Statistic, 
  Typography, 
  Select,
  Spin,
  message
} from 'antd';
import { 
  PieChart, 
  Pie, 
  Cell, 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend,
  ResponsiveContainer
} from 'recharts';
import { taskAPI } from '../services/api';

const { Title } = Typography;
const { Option } = Select;

const Statistics = () => {
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [chartType, setChartType] = useState('completion');

  useEffect(() => {
    fetchStatistics();
  }, []);

  const fetchStatistics = async () => {
    try {
      const data = await taskAPI.getStatistics();
      setStatistics(data);
    } catch (error) {
      message.error('获取统计数据失败');
    } finally {
      setLoading(false);
    }
  };

  const getStatusData = () => {
    if (!statistics?.statusCount) return [];
    
    return Object.entries(statistics.statusCount).map(([key, value]) => ({
      name: key === 'COMPLETED' ? '已完成' : '未完成',
      value: value,
      color: key === 'COMPLETED' ? '#52c41a' : '#1890ff'
    }));
  };

  const getPriorityData = () => {
    if (!statistics?.priorityCount) return [];
    
    const priorityMap = {
      'HIGH': { name: '高优先级', color: '#ff4d4f' },
      'MEDIUM': { name: '中优先级', color: '#888888' },
      'LOW': { name: '低优先级', color: '#52c41a' }
    };
    
    return Object.entries(statistics.priorityCount).map(([key, value]) => ({
      name: priorityMap[key]?.name || key,
      value: value,
      color: priorityMap[key]?.color || '#888888'
    }));
  };

  const getTrendData = () => {
    if (!statistics?.dailyCreated || !statistics?.dailyCompleted) return [];
    
    const last7Days = [];
    const today = new Date();
    
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      const dateStr = date.toISOString().split('T')[0];
      
      last7Days.push({
        date: `${date.getMonth() + 1}/${date.getDate()}`,
        created: statistics.dailyCreated[dateStr] || 0,
        completed: statistics.dailyCompleted[dateStr] || 0
      });
    }
    
    return last7Days;
  };

  const renderChart = () => {
    switch (chartType) {
      case 'completion':
        return (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={getStatusData()}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {getStatusData().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        );
      
      case 'priority':
        return (
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={getPriorityData()}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {getPriorityData().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        );
      
      case 'trend':
        return (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={getTrendData()}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="created" 
                stroke="#1890ff" 
                name="新增任务"
                strokeWidth={2}
              />
              <Line 
                type="monotone" 
                dataKey="completed" 
                stroke="#52c41a" 
                name="完成任务"
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
        );
      
      default:
        return null;
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!statistics) {
    return null;
  }

  return (
    <div className="statistics-container">
      <Title level={2} style={{ marginBottom: 32 }}>数据统计</Title>

      <div className="stats-grid">
        <Col span={6}>
          <Card>
            <Statistic
              title="总任务数"
              value={statistics.totalTasks}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        
        <Col span={6}>
          <Card>
            <Statistic
              title="已完成"
              value={statistics.completedTasks}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        
        <Col span={6}>
          <Card>
            <Statistic
              title="未完成"
              value={statistics.totalTasks - statistics.completedTasks}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        
        <Col span={6}>
          <Card>
            <Statistic
              title="完成率"
              value={statistics.completionRate}
              precision={1}
              suffix="%"
              valueStyle={{ 
                color: statistics.completionRate >= 70 ? '#52c41a' : 
                       statistics.completionRate >= 40 ? '#faad14' : '#ff4d4f' 
              }}
            />
          </Card>
        </Col>
      </div>

      <Card style={{ marginTop: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <Title level={3} style={{ margin: 0 }}>
            {chartType === 'completion' && '完成状态分布'}
            {chartType === 'priority' && '优先级分布'}
            {chartType === 'trend' && '近7天趋势'}
          </Title>
          
          <Select value={chartType} onChange={setChartType} style={{ width: 140 }}>
            <Option value="completion">完成状态</Option>
            <Option value="priority">优先级分布</Option>
            <Option value="trend">趋势图</Option>
          </Select>
        </div>

        {renderChart()}
      </Card>
    </div>
  );
};

export default Statistics;