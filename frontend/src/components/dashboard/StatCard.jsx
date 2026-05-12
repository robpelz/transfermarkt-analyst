const StatCard = ({ title, value, icon, color, trend }) => {
  const colorClasses = {
    blue: 'bg-blue-500/10 text-blue-400',
    purple: 'bg-purple-500/10 text-purple-400',
    green: 'bg-green-500/10 text-green-400',
    yellow: 'bg-yellow-500/10 text-yellow-400',
  };

  return (
    <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-6">
      <div className="flex items-center justify-between mb-4">
        <div className={`p-3 rounded-lg ${colorClasses[color]}`}>
          {icon}
        </div>
        <span className="text-xs text-green-400 bg-green-500/10 px-2 py-1 rounded">
          {trend}
        </span>
      </div>
      <h3 className="text-2xl font-bold text-white">{value}</h3>
      <p className="text-[#b8baff] text-sm mt-1">{title}</p>
    </div>
  );
};

export default StatCard;