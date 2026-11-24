import React, { useState, useEffect } from 'react';
import { BadgeCheck, Sparkles } from 'lucide-react';

interface PackingListProps {
  destination: string;
  days: number;
}

export const PackingList: React.FC<PackingListProps> = ({ destination, days }) => {
  const [items, setItems] = useState<string[]>([]);
  const [isFromBackend, setIsFromBackend] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  // Local fallback generator (used if Java backend is offline)
  const generateFallbackList = () => {
    const baseItems = ['Toothbrush', 'Phone Charger', 'Power Bank', `Underwear x${days}`, `Socks x${days}`];
    const destLower = (destination || '').toLowerCase();

    let specificItems: string[] = [];
    if (destLower.includes('beach') || destLower.includes('goa') || destLower.includes('miami') || destLower.includes('maldives') || destLower.includes('island')) {
      specificItems = ['Sunscreen', 'Swimwear', 'Sunglasses', 'Flip-flops', 'Beach Towel'];
    } else if (destLower.includes('mountain') || destLower.includes('snow') || destLower.includes('himalaya') || destLower.includes('alps') || destLower.includes('manali')) {
      specificItems = ['Thermal Wear', 'Trekking Shoes', 'Heavy Jacket', 'Gloves', 'Beanie'];
    } else {
      specificItems = ['Comfortable Walking Shoes', 'Umbrella', 'Casual Outfits', 'Light Jacket'];
    }

    return [...baseItems, ...specificItems];
  };

  useEffect(() => {
    let isMounted = true;
    const fetchPackingList = async () => {
      setIsLoading(true);
      try {
        // Attempt to fetch from YourTripy Java Backend (Strategy Pattern endpoint)
        const response = await fetch(
          `http://localhost:8080/api/trips/packing-list?destination=${encodeURIComponent(destination || 'Default')}&days=${days}`,
          { signal: AbortSignal.timeout(1500) } // Fast 1.5s timeout for seamless fallback
        );
        if (response.ok) {
          const result = await response.json();
          if (isMounted && result.data && Array.isArray(result.data)) {
            setItems(result.data);
            setIsFromBackend(true);
            setIsLoading(false);
            return;
          }
        }
      } catch {
        // Backend offline or unreachable -> gracefully use local fallback
      }

      if (isMounted) {
        setItems(generateFallbackList());
        setIsFromBackend(false);
        setIsLoading(false);
      }
    };

    fetchPackingList();

    return () => {
      isMounted = false;
    };
  }, [destination, days]);

  return (
    <div className="bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm border border-gray-200 dark:border-gray-700 rounded-xl p-6 mt-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold text-gray-900 dark:text-white flex items-center">
          <BadgeCheck className="w-5 h-5 mr-2 text-indigo-500" />
          Smart Packing List
        </h3>
        {isFromBackend && (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20">
            <Sparkles className="w-3.5 h-3.5" />
            Java Strategy Pattern Active
          </span>
        )}
      </div>

      {isLoading ? (
        <p className="text-sm text-gray-500">Curating packing list...</p>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
          {items.map((item, index) => (
            <div key={index} className="flex items-center space-x-2 text-sm text-gray-700 dark:text-gray-300">
              <input type="checkbox" className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500" />
              <span>{item}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default PackingList;
