import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { ScoreGauge } from '../components/ui/ScoreGauge';

describe('ScoreGauge Component', () => {
  it('renders similarity score and concise reasons', () => {
    const reasons = [
      'Category matches: Electronics',
      'Title matches keywords: macbook, pro',
      'Location proximity: Within 400 meters',
    ];

    render(
      <ScoreGauge
        score={85.4}
        reasons={reasons}
      />
    );

    expect(screen.getByText('85%')).toBeInTheDocument();
    expect(screen.getByText(/Category matches: Electronics/)).toBeInTheDocument();
    expect(screen.getByText(/Location proximity: Within 400 meters/)).toBeInTheDocument();
    expect(screen.getByText(/does not guarantee legal ownership/i)).toBeInTheDocument();
  });
});
