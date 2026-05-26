const StatCard = ({ title, value, color }) => {
  return (
    <div style={{
      backgroundColor: '#1a1a2a',
      border: '1px solid #2a2a3a',
      borderRadius: '0.75rem',
      padding: '1.5rem'
    }}>
      <div style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: '3rem',
        height: '3rem',
        borderRadius: '0.5rem',
        marginBottom: '1rem',
        backgroundColor: `${color}20`
      }}>
        <div style={{
          width: '1.5rem',
          height: '1.5rem',
          backgroundColor: color,
          borderRadius: '0.25rem'
        }}></div>
      </div>
      <div style={{
        fontSize: '2rem',
        fontWeight: 'bold',
        color: 'white',
        marginBottom: '0.25rem'
      }}>{value}</div>
      <div style={{
        color: '#b8baff',
        fontSize: '0.875rem'
      }}>{title}</div>
      <div style={{
        fontSize: '0.75rem',
        color: '#10b981',
        backgroundColor: 'rgba(16, 185, 129, 0.1)',
        padding: '0.25rem 0.5rem',
        borderRadius: '0.25rem',
        display: 'inline-block',
        marginTop: '0.5rem'
      }}>+12% this month</div>
    </div>
  );
};

export default StatCard;