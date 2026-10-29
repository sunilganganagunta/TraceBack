import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { Badge } from '../components/ui/Badge';

describe('Badge Component', () => {
  it('renders LOST badge with rose styling', () => {
    render(<Badge type="LOST" />);
    expect(screen.getByText('LOST')).toBeInTheDocument();
  });

  it('renders FOUND badge with emerald styling', () => {
    render(<Badge type="FOUND" />);
    expect(screen.getByText('FOUND')).toBeInTheDocument();
  });

  it('renders status badge properly formatted', () => {
    render(<Badge status="CLAIM_PENDING" />);
    expect(screen.getByText('CLAIM PENDING')).toBeInTheDocument();
  });
});
