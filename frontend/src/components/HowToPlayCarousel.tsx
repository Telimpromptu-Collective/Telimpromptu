import React from 'react';
import styles from "../styles/components.module.css";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

const HowToPlayCarousel: React.FC = () => {
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 2000,
    arrows: false
  };

  return (
    <div className={styles.carouselContainer}> 
      <h3><i>✨ How to Play ✨</i></h3>
      <Slider {...settings} className={styles.carousel}>
        <div>
          <img src="/images/carousel_slide_1.png" alt="Slide 1" />
          <h3>1. Choose a topic</h3>
        </div>
        <div>
          <img src="/images/carousel_slide_2.png" alt="Slide 2" />
          <h3>2. A player comes up with the headline</h3>
        </div>
        <div>
          <img src="/images/carousel_slide_3.png" alt="Slide 3" />
          <h3>3. Players respond to prompts</h3>
        </div>

        <div>
          <img src="/images/carousel_slide_4.png" alt="Slide 4" />
          <h3>4. Everyone reads the final script off the teleprompter</h3>
        </div>
      </Slider>
    </div>
  );
};

export default HowToPlayCarousel;
