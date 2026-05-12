import { Link } from 'react-router-dom';
import { MapPin, Calendar } from 'lucide-react';

const ClubCard = ({ club }) => {
  const teamInitials = club.strTeam
    ?.split(' ')
    .map(word => word[0])
    .join('')
    .substring(0, 2)
    .toUpperCase() || '⚽';

  return (
    <Link to={`/clubs/${club.idTeam}`}>
      <div className="bg-[#1a1a2a] border border-[#2a2a3a] rounded-xl p-5 hover:border-[#6666ff] transition-all group cursor-pointer">
        <div className="flex items-start gap-3 mb-4">
          <div className="w-16 h-16 rounded-full bg-[#0c0c16] flex items-center justify-center overflow-hidden">
            {club.localLogoUrl ? (
              <img 
                src={club.localLogoUrl} 
                alt={club.strTeam}
                className="w-full h-full object-contain"
                onError={(e) => {
                  e.target.onerror = null;
                  e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(club.strTeam)}&background=6666ff&color=fff&size=64`;
                }}
              />
            ) : (
              <span className="text-white text-xl font-bold">{teamInitials}</span>
            )}
          </div>
          <div className="flex-1">
            <h3 className="font-semibold text-lg text-white group-hover:text-[#6666ff]">
              {club.strTeam}
            </h3>
            {club.strStadium && (
              <p className="text-xs text-[#b8baff] flex items-center gap-1 mt-1">
                <MapPin className="w-3 h-3" />
                {club.strStadium}
              </p>
            )}
            {club.intFormedYear && (
              <p className="text-xs text-[#b8baff] flex items-center gap-1 mt-1">
                <Calendar className="w-3 h-3" />
                Gegründet: {club.intFormedYear}
              </p>
            )}
          </div>
        </div>
        <div className="flex justify-between items-center pt-2 border-t border-[#2a2a3a]">
          <span className="text-sm text-[#b8baff]">Details ansehen</span>
          <span className="text-sm text-[#6666ff] group-hover:text-[#b8baff]">→</span>
        </div>
      </div>
    </Link>
  );
};

export default ClubCard;