import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { 
  Card, 
  Button, 
  Space, 
  Typography, 
  Tag, 
  Descriptions,
  message,
  Spin
} from 'antd';
import { 
  ArrowLeftOutlined, 
  EditOutlined, 
  DeleteOutlined,
  CheckOutlined,
  CloseOutlined
} from '@ant-design/icons';
import { taskAPI } from '../services/api';
import dayjs from 'dayjs';

const { Title, Text, Paragraph } = Typography;

const TaskDetail = () => {
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    fetchTask();
  }, [id]);

  const fetchTask = async () => {
    try {
      const data = await taskAPI.getTask(id);
      setTask(data);
    } catch (error) {
      message.error('获取任务详情失败');
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleStatus = async () => {
    setActionLoading(true);
    try {
      await taskAPI.toggleTaskStatus(id);
      message.success('任务状态更新成功');
      fetchTask();
    } catch (error) {
      message.error('更新任务状态失败');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('确定删除该任务？删除后不可恢复')) {
      setActionLoading(true);
      try {
        await taskAPI.deleteTask(id);
        message.success('任务删除成功');
        navigate('/');
      } catch (error) {
        message.error('删除任务失败');
      } finally {
        setActionLoading(false);
      }
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

  const formatDateTime = (dateTime) => {
    if (!dateTime) return null;
    return dayjs(dateTime).format('YYYY-MM-DD HH:mm');
  };

  const isOverdue = (deadline, status) => {
    return deadline && status === 'PENDING' && dayjs(deadline).isBefore(dayjs());
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!task) {
    return null;
  }

  return (
    <div className="task-form-container">
      <div style={{ marginBottom: 24 }}>
        <Space>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate('/')}
            type="text"
          >
            返回列表
          </Button>
          <Title level={2} style={{ margin: 0 }}>任务详情</Title>
        </Space>
      </div>

      <Card>
        <div style={{ marginBottom: 24 }}>
          <Space align="start" style={{ width: '100%' }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 16 }}>
                <Title level={3} style={{ margin: 0 }}>
                  {task.title}
                </Title>
                <Tag 
                  color={getPriorityColor(task.priority)}
                  style={{ fontSize: '14px' }}
                >
                  {getPriorityText(task.priority)}优先级
                </Tag>
                <Tag 
                  color={task.status === 'COMPLETED' ? 'green' : 'blue'}
                  style={{ fontSize: '14px' }}
                >
                  {task.status === 'COMPLETED' ? '已完成' : '未完成'}
                </Tag>
                {isOverdue(task.deadline, task.status) && (
                  <Tag color="red" style={{ fontSize: '14px' }}>
                    逾期
                  </Tag>
                )}
              </div>

              {task.description && (
                <div style={{ marginBottom: 16 }}>
                  <Text strong>任务描述：</Text>
                  <Paragraph style={{ marginTop: 8, marginBottom: 0 }}>
                    {task.description}
                  </Paragraph>
                </div>
              )}
            </div>

            <Space direction="vertical">
              <Button
                type={task.status === 'COMPLETED' ? 'default' : 'primary'}
                icon={task.status === 'COMPLETED' ? <CloseOutlined /> : <CheckOutlined />}
                onClick={handleToggleStatus}
                loading={actionLoading}
              >
                {task.status === 'COMPLETED' ? '标记未完成' : '标记完成'}
              </Button>
              
              <Button
                icon={<EditOutlined />}
                onClick={() => navigate(`/task/edit/${id}`)}
              >
                编辑
              </Button>
              
              <Button
                danger
                icon={<DeleteOutlined />}
                onClick={handleDelete}
                loading={actionLoading}
              >
                删除
              </Button>
            </Space>
          </Space>
        </div>

        <Descriptions column={1} bordered>
          <Descriptions.Item label="任务状态">
            <Tag color={task.status === 'COMPLETED' ? 'green' : 'blue'}>
              {task.status === 'COMPLETED' ? '已完成' : '未完成'}
            </Tag>
            {task.status === 'COMPLETED' && task.completedAt && (
              <Text type="secondary" style={{ marginLeft: 8 }}>
                完成于：{formatDateTime(task.completedAt)}
              </Text>
            )}
          </Descriptions.Item>

          <Descriptions.Item label="优先级">
            <Tag color={getPriorityColor(task.priority)}>
              {getPriorityText(task.priority)}优先级
            </Tag>
          </Descriptions.Item>

          <Descriptions.Item label="截止时间">
            {task.deadline ? (
              <div>
                <Text>{formatDateTime(task.deadline)}</Text>
                {isOverdue(task.deadline, task.status) && (
                  <Tag color="red" style={{ marginLeft: 8 }}>
                    已逾期
                  </Tag>
                )}
              </div>
            ) : (
              <Text type="secondary">无截止时间</Text>
            )}
          </Descriptions.Item>

          <Descriptions.Item label="创建时间">
            {formatDateTime(task.createdAt)}
          </Descriptions.Item>

          <Descriptions.Item label="更新时间">
            {formatDateTime(task.updatedAt)}
          </Descriptions.Item>

          {task.remainingMinutes !== null && task.status === 'PENDING' && (
            <Descriptions.Item label="剩余时间">
              {task.remainingMinutes > 0 ? (
                <Text>
                  还有 {Math.floor(task.remainingMinutes / 60)} 小时 {task.remainingMinutes % 60} 分钟
                </Text>
              ) : (
                <Text type="danger">已过期</Text>
              )}
            </Descriptions.Item>
          )}
        </Descriptions>
      </Card>
    </div>
  );
};

export default TaskDetail;