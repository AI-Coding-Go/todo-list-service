import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Layout, Menu, Typography } from 'antd';
import { 
  CheckSquareOutlined, 
  BarChartOutlined, 
  BellOutlined 
} from '@ant-design/icons';

const { Header: AntHeader } = Layout;
const { Title } = Typography;

const Header = () => {
  const location = useLocation();

  const menuItems = [
    {
      key: '/',
      icon: <CheckSquareOutlined />,
      label: <Link to="/">任务列表</Link>,
    },
    {
      key: '/statistics',
      icon: <BarChartOutlined />,
      label: <Link to="/statistics">数据统计</Link>,
    },
    {
      key: '/reminders',
      icon: <BellOutlined />,
      label: <Link to="/reminders">提醒中心</Link>,
    },
  ];

  return (
    <AntHeader className="header-container" style={{ 
      display: 'flex', 
      justifyContent: 'space-between', 
      alignItems: 'center',
      padding: '0 24px'
    }}>
      <Title level={3} className="header-title" style={{ margin: 0 }}>
        📝 待办清单
      </Title>
      
      <Menu
        mode="horizontal"
        selectedKeys={[location.pathname]}
        items={menuItems}
        style={{ 
          border: 'none',
          background: 'transparent',
          flex: 1,
          justifyContent: 'center'
        }}
      />
      
      <div style={{ width: '120px' }} />
    </AntHeader>
  );
};

export default Header;