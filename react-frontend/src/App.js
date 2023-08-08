import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [maze, setMaze] = useState([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/generate-maze')
      .then((response) => response.json())
      .then((data) => {
        setMaze(data);
      })
      .catch((error) => {
        console.error('Error fetching maze data:', error);
      });
  }, []);

  const getCellClass = (cellValue) => {
    if (cellValue === 0) {
      return 'wall-cell';
    } else if (cellValue === 16) {
      return 'visited-cell';
    } else if (cellValue === 31) {
      return 'path-cell';
    }

    const directions = ['north', 'east', 'south', 'west'];
    const pathClasses = [];
    directions.forEach((dir, index) => {
      if ((cellValue & (1 << index)) !== 0) {
        pathClasses.push(`${dir}-path`);
      }
    });

    return pathClasses.join(' ');
  };

  return (
    <div className="app-container">
      <header className="header">
        <h1>Maze Runner</h1>
      </header>
      <div className="maze-container">
        <div className="transparent-square"></div>
        <div className="maze">
          {maze.map((row, rowIndex) => (
            <div key={`row-${rowIndex}`} className="maze-row">
              {row.map((cellValue, columnIndex) => (
                <div
                  key={`cell-${rowIndex}-${columnIndex}`}
                  className={`maze-cell ${getCellClass(cellValue)}`}
                />
              ))}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default App;
