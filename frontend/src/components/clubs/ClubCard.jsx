import { Link } from 'react-router-dom';

const ClubCard = ({ club }) => {
  const teamInitials = club.strTeam
    ?.split(' ')
    .map(word => word[0])
    .join('')
    .substring(0, 2)
    .toUpperCase() || '⚽';

  return (
    <Link to={`/clubs/${club.idTeam}`}>
      <div className="club-header">
        <div className="club-logo">
          {club.localLogoUrl ? (
            <img 
              src={club.localLogoUrl} 
              alt={club.strTeam}
              style={{ width: '100%', height: '100%', objectFit: 'contain' }}
              onError={(e) => {
                e.target.onerror = null;
                e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(club.strTeam)}&background=6666ff&color=fff&size=64`;
              }}
            />
          ) : (
            <span style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'white' }}>{teamInitials}</span>
          )}
        </div>
        <div style={{ flex: 1 }}>
          <div className="club-name">{club.strTeam}</div>
          {club.strStadium && (
            <div className="club-stadium">
              <span>📍</span>
              <span>{club.strStadium}</span>
            </div>
          )}
          {club.intFormedYear && (
            <div className="club-stadium">
              <span>📅</span>
              <span>Gegründet: {club.intFormedYear}</span>
            </div>
          )}
        </div>
      </div>
      <div className="club-footer">
        <span className="club-detail-link">Details ansehen</span>
        <span className="club-arrow">→</span>
      </div>
    </Link>
  );
};

export default ClubCard;