using DetermineShippingCost;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xunit;

namespace Tests
{
    public class ClassAssignmentTests
    {
        [Theory]
        //happy flow testing
        [InlineData(true, "Ground", 2000, 0)]
        [InlineData(true, "Ground", 1000, 100)]
        [InlineData(true, "InStore", 1000, 50)]
        [InlineData(true, "NextDayAir", 1200, 250)]
        [InlineData(true, "SecondDayAir", 800, 125)]
        [InlineData(true, "InvalidType", 500, 0)]
        [InlineData(false, "InvalidType", 500, 0)]
        [InlineData(false, "Ground", 2000, 0)]
        [InlineData(false, "InStore", 1500, 0)]
        [InlineData(false, "NextDayAir", 500, 0)]
        //boundary testing
        [InlineData(true, "Ground", 1500.01, 0)]
        [InlineData(true, "Ground", 1500, 100)]
        [InlineData(true, "InStore", 1499.99, 50)]
        //unhapy flow testing
        [InlineData(true, "InvalidType", 2000, 0)]
        [InlineData(true, "InvalidType", 1000, 0)]
        [InlineData(true, "Ground", 0, 100)]
        [InlineData(true, "Ground", -500, 100)]
        [InlineData(false, "", 1000, 0)]
        [InlineData(true, "", 1000, 0)]
        [InlineData(true, null, 1000, 0)]
        public void ShippingCosts_CalculatesCorrectShippingCost(bool calculateShippingCosts, string typeOfShippingCosts, double totalPrice, double expectedShippingCost)
        {
            // Arrange
            ClassAssignmentAvans avans = new ClassAssignmentAvans();

            // Act
            double actualShippingCost = avans.ShippingCosts(calculateShippingCosts, typeOfShippingCosts, totalPrice);

            // Assert
            Assert.Equal(expectedShippingCost, actualShippingCost);
        }
    }
}
