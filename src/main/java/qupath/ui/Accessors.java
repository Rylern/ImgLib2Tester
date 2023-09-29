package qupath.ui;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.Random;

public class Accessors {
    public static void DrawWhitePixels() {
        final int[] dimensions = new int[] { 400, 320 };
        final Img<UnsignedByteType> img = new ArrayImgFactory< UnsignedByteType >()
                .create( dimensions, new UnsignedByteType() );

        final RandomAccess< UnsignedByteType > r = img.randomAccess();
        final Random random = new Random();
        for ( int i = 0; i < 1000; ++i )
        {
            final int x = ( int ) ( random.nextFloat() * img.max( 0 ) );
            final int y = ( int ) ( random.nextFloat() * img.max( 1 ) );
            r.setPosition( x, 0 );
            r.setPosition( y, 1 );
            final UnsignedByteType t = r.get();
            t.set( 255 );
        }

        ImageJFunctions.show( img );
    }

    public static void DrawWhitePixelsGeneric() {
        final Img<ARGBType> img = new ArrayImgFactory< ARGBType >()
                .create( new int[] {400, 320, 100}, new ARGBType() );
        draw( img, new ARGBType( 0xffffffff ) );
        ImageJFunctions.show( img );
    }

    private static < T extends Type< T >> void draw(final RandomAccessibleInterval< T > img, final T white )
    {
        final int n = img.numDimensions();
        final long[] min = new long[ n ];
        img.min( min );
        final long[] scale = new long[ n ];
        for ( int d = 0; d < n; ++d )
            scale[ d ] = img.max( d ) - min[ d ];
        final long[] pos = new long[ n ];

        final RandomAccess< T > r = img.randomAccess();
        final Random random = new Random();
        for ( int i = 0; i < 1000; ++i )
        {
            for ( int d = 0; d < n; ++d )
                pos[ d ] = min[ d ] + ( long ) ( random.nextFloat() * scale[ d ] );
            r.setPosition( pos );
            r.get().set( white );
        }
    }

    public static void FindMaximumValue() {
        final int[] dimensions = new int[] { 400, 320 };
        final Img<UnsignedByteType> img = new ArrayImgFactory< UnsignedByteType >()
                .create( dimensions, new UnsignedByteType() );
        final Cursor< UnsignedByteType > cursor = img.cursor();
        int max = 0;
        while ( cursor.hasNext() )
        {
            final UnsignedByteType t = cursor.next();
            max = Math.max( t.get(), max );
        }
        System.out.println( "max = " + max );
    }

    public static void FindMaximumValueAndLocation() {
        final int[] dimensions = new int[] { 400, 320 };
        final Img<UnsignedByteType> img = new ArrayImgFactory< UnsignedByteType >()
                .create( dimensions, new UnsignedByteType() );
        final Cursor< UnsignedByteType > cursor = img.cursor();
        int max = 0;
        final long[] pos = new long[2];
        while ( cursor.hasNext() )
        {
            cursor.fwd();
            final UnsignedByteType t = cursor.get();
            if ( t.get() > max )
            {
                max = t.get();
                cursor.localize( pos );
            }
        }
        System.out.println( "max = " + max );
        System.out.println( "found at ( " + pos[0] + ", " + pos[1] + ")" );
    }

    public static void DrawMandelbrot() {
        final int[] dimensions = new int[] { 600, 400 };
        final Img< UnsignedByteType > img = new ArrayImgFactory< UnsignedByteType >()
                .create( dimensions, new UnsignedByteType() );

        final RealRandomAccess<UnsignedByteType> mb = new MandelbrotRealRandomAccess();

        final double scale = 0.005;
        final double[] offset = new double[] { -2, -1 };

        final Cursor< UnsignedByteType > cursor = img.localizingCursor();
        while( cursor.hasNext() )
        {
            cursor.fwd();
            for ( int d = 0; d < 2; ++d )
                mb.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
            cursor.get().set( mb.get() );
        }

        ImageJFunctions.show( img );
    }
}
