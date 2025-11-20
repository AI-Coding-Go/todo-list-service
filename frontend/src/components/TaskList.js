import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Card, 
  Button, 
  Select, 
  Checkbox, 
  Space, 
  Tag, 
  Typography, 
  Empty,
  Spin,
  message,
  FloatButton
} from 'antd';
import { 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';
import { taskAPI } from '../services/api';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;

const TaskList = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState('all');
  const [sortBy, setSortBy] = useState('created');
  const navigate = useNavigate();

  useEffect(() => {
    fetchTasks();
  }, [statusFilter, sortBy]);

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const params = {
        status: statusFilter === 'all' ? undefined : statusFilter,
        sortBy
      };
      const data = await taskAPI.getTasks(params);
      setTasks(data);
    } catch (error) {
      message.error('获取任务列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleStatus = async (taskId) => {
    try {
      await taskAPI.toggleTaskStatus(taskId);
      message.success('任务状态更新成功');
      fetchTasks();
    } catch (error) {
      message.error('更新任务状态失败');
    }
  };

  const handleDelete = async (taskId) => {
    try {
      await taskAPI.deleteTask(taskId);
      message.success('任务删除成功');
      fetchTasks();
    } catch (error) {
      message.error('删除任务失败');
    }
  };

  const getPriorityColor = (priority) => {
    const colors = {
      HIGH: '#ff4d4f',
      MEDIUM: '#888888',
      LOW: '#52c41a'
    };
    return colors[priority] || '#888888';
  };

  const getPriorityText = (priority) => {
    const texts = {
      HIGH: '高',
      MEDIUM: '中',
      LOW: '低'
    };
    return texts[priority] || '中';
  };

  const formatDeadline = (deadline) => {
    if (!deadline) return null;
    return dayjs(deadline).format('MM-DD HH:mm');
  };

  const isOverdue = (deadline, status) => {
    return deadline && status === 'PENDING' && dayjs(deadline).isBefore(dayjs());
  };

  const getTaskCardClass = (task) => {
    let className = 'task-item';
    if (task.status === 'COMPLETED') {
      className += ' task-completed';
    }
    if (isOverdue(task.deadline, task.status)) {
      className += ' overdue-task';
    }
    return className;
  };

  return (
    <div className="task-list-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>任务列表</Title>
        <Button 
          type="primary" 
          icon={<PlusOutlined />}
          onClick={() => navigate('/task/new')}
        >
          新建任务
        </Button>
      </div>

      <div className="task-filters">
        <Space>
          <Text>状态筛选：</Text>
          <Select value={statusFilter} onChange={setStatusFilter} style={{ width: 120 }}>
            <Option value="all">全部</Option>
            <Option value="pending">未完成</Option>
            <Option value="completed">已完成</Option>
          </Select>
        </Space>

        <Space>
          <Text>排序方式：</Text>
          <Select value={sortBy} onChange={setSortBy} style={{ width: 140 }}>
            <Option value="created">创建时间</Option>
            <Option value="deadline">截止时间</Option>
            <Option value="priority">优先级</Option>
          </Select>
        </Space>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <Spin size="large" />
        </div>
      ) : tasks.length === 0 ? (
        <Empty 
          description="暂无任务" 
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      ) : (
        <div>
          {tasks.map(task => (
            <Card 
              key={task.id}
              className={getTaskCardClass(task)}
              hoverable
              onClick={() => navigate(`/task/${task.id}`)}
            >
              <div className="task-header">
                <Checkbox
                  checked={task.status === 'COMPLETED'}
                  onChange={(e) => {
                    e.stopPropagation();
                    handleToggleStatus(task.id);
                  }}
                  className="task-checkbox"
                />
                
                <div style={{ flex: 1 }}>
                  <div className="task-title">
                    {task.title}
                  </div>
                  
                  <div className="task-meta">
                    <Space>
                      <Tag 
                        color={getPriorityColor(task.priority)}
                        style={{ margin: 0 }}
                      >
                        {getPriorityText(task.priority)}优先级
                      </Tag>
                      
                      {task.deadline && (
                        <Space size={4}>
                          <ClockCircleOutlined />
                          <Text type={isOverdue(task.deadline, task.status) ? 'danger' : 'secondary'}>
                            {formatDeadline(task.deadline)}
                          </Text>
                          {isOverdue(task.deadline, task.status) && (
                            <Tag color="red">逾期</Tag>
                          )}
                        </Space>
                      )}
                    </Space>
                    
                    <div className="task-actions">
                      <Button
                        type="text"
                        icon={<EditOutlined />}
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/task/edit/${task.id}`);
                        }}
                      />
                      <Button
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          if (window.confirm('确定删除该任务？删除后不可恢复')) {
                            handleDelete(task.id);
                          }
                        }}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      <FloatButton
        icon={<PlusOutlined />}
        type="primary"
        onClick={() => navigate('/task/new')}
        className="fab-button"
      />
    </div>
  );
};

export default TaskList;