import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { 
  Card, 
  Form, 
  Input, 
  Select, 
  DatePicker, 
  Button, 
  Space,
  message,
  Typography
} from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { taskAPI } from '../services/api';
import dayjs from 'dayjs';

const { Title } = Typography;
const { TextArea } = Input;
const { Option } = Select;

const TaskForm = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [isEdit, setIsEdit] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    if (id) {
      setIsEdit(true);
      fetchTask();
    }
  }, [id]);

  const fetchTask = async () => {
    try {
      const task = await taskAPI.getTask(id);
      form.setFieldsValue({
        title: task.title,
        description: task.description,
        priority: task.priority,
        deadline: task.deadline ? dayjs(task.deadline) : null,
      });
    } catch (error) {
      message.error('获取任务信息失败');
      navigate('/');
    }
  };

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const submitData = {
        ...values,
        deadline: values.deadline ? values.deadline.toISOString() : null,
      };

      if (isEdit) {
        await taskAPI.updateTask(id, submitData);
        message.success('任务更新成功');
      } else {
        await taskAPI.createTask(submitData);
        message.success('任务创建成功');
      }
      
      navigate('/');
    } catch (error) {
      if (error.response?.data?.details) {
        // 处理验证错误
        const errors = error.response.data.details;
        Object.keys(errors).forEach(key => {
          form.setFields([
            {
              name: key,
              errors: [errors[key]],
            },
          ]);
        });
      } else {
        message.error(isEdit ? '更新任务失败' : '创建任务失败');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/');
  };

  return (
    <div className="task-form-container">
      <div style={{ marginBottom: 24 }}>
        <Space>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={handleCancel}
            type="text"
          >
            返回
          </Button>
          <Title level={2} style={{ margin: 0 }}>
            {isEdit ? '编辑任务' : '新建任务'}
          </Title>
        </Space>
      </div>

      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{
          priority: 'MEDIUM',
        }}
      >
        <Form.Item
          name="title"
          label="任务标题"
          rules={[
            { required: true, message: '请输入任务标题' },
            { max: 50, message: '任务标题不能超过50个字符' },
          ]}
        >
          <Input placeholder="请输入任务标题" />
        </Form.Item>

        <Form.Item
          name="description"
          label="任务描述"
          rules={[
            { max: 500, message: '任务描述不能超过500个字符' },
          ]}
        >
          <TextArea
            rows={4}
            placeholder="请输入任务描述（可选）"
            showCount
            maxLength={500}
          />
        </Form.Item>

        <Form.Item
          name="priority"
          label="优先级"
          rules={[{ required: true, message: '请选择优先级' }]}
        >
          <Select placeholder="请选择优先级">
            <Option value="HIGH">
              <span style={{ color: '#ff4d4f' }}>● 高优先级</span>
            </Option>
            <Option value="MEDIUM">
              <span style={{ color: '#888888' }}>● 中优先级</span>
            </Option>
            <Option value="LOW">
              <span style={{ color: '#52c41a' }}>● 低优先级</span>
            </Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="deadline"
          label="截止时间"
        >
          <DatePicker
            showTime
            placeholder="请选择截止时间（可选）"
            style={{ width: '100%' }}
            disabledDate={(current) => {
              return current && current < dayjs().startOf('day');
            }}
          />
        </Form.Item>

        <div className="form-actions">
          <Space>
            <Button onClick={handleCancel}>
              取消
            </Button>
            <Button 
              type="primary" 
              htmlType="submit" 
              loading={loading}
            >
              {isEdit ? '保存' : '创建'}
            </Button>
          </Space>
        </div>
      </Form>
    </div>
  );
};

export default TaskForm;